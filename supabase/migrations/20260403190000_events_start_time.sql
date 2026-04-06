-- Optional event start time (local wall clock, HH:mm) + snapshot on reservations.

ALTER TABLE public.events
  ADD COLUMN IF NOT EXISTS start_time text;

ALTER TABLE public.reservations
  ADD COLUMN IF NOT EXISTS event_start_time_snapshot text;

COMMENT ON COLUMN public.events.start_time IS 'Local start time as HH:mm (24h), empty/null = unspecified';
COMMENT ON COLUMN public.reservations.event_start_time_snapshot IS 'Copy of event start_time at booking time';

DROP FUNCTION IF EXISTS public.book_event(uuid, text, integer, text, date, text);

CREATE OR REPLACE FUNCTION public.book_event(
  p_event_id uuid,
  p_user_key text,
  p_qty integer,
  p_title text,
  p_iso date,
  p_start_time text,
  p_loc text
)
RETURNS json
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  v_res_id uuid;
BEGIN
  IF p_qty <= 0 THEN
    RETURN json_build_object('ok', false, 'reason', 'bad_quantity');
  END IF;

  UPDATE public.events e
  SET tickets_reserved = e.tickets_reserved + p_qty
  WHERE e.id = p_event_id
    AND NOT e.canceled
    AND e.tickets_reserved + p_qty <= e.capacity;

  IF NOT FOUND THEN
    RETURN json_build_object('ok', false, 'reason', 'not_available');
  END IF;

  INSERT INTO public.reservations (
    event_id, user_key, quantity,
    event_title_snapshot, event_iso_date_snapshot, event_location_snapshot,
    event_start_time_snapshot
  ) VALUES (
    p_event_id, p_user_key, p_qty, p_title, p_iso, p_loc,
    NULLIF(trim(coalesce(p_start_time, '')), '')
  ) RETURNING id INTO v_res_id;

  RETURN json_build_object('ok', true, 'reservation_id', v_res_id);
END;
$$;

REVOKE ALL ON FUNCTION public.book_event(uuid, text, integer, text, date, text, text) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.book_event(uuid, text, integer, text, date, text, text) TO anon;
GRANT EXECUTE ON FUNCTION public.book_event(uuid, text, integer, text, date, text, text) TO authenticated;
