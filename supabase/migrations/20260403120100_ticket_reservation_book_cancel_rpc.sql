CREATE OR REPLACE FUNCTION public.book_event(
  p_event_id uuid,
  p_user_key text,
  p_qty integer,
  p_title text,
  p_iso date,
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
    event_title_snapshot, event_iso_date_snapshot, event_location_snapshot
  ) VALUES (
    p_event_id, p_user_key, p_qty, p_title, p_iso, p_loc
  ) RETURNING id INTO v_res_id;

  RETURN json_build_object('ok', true, 'reservation_id', v_res_id);
END;
$$;

REVOKE ALL ON FUNCTION public.book_event(uuid, text, integer, text, date, text) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.book_event(uuid, text, integer, text, date, text) TO anon;
GRANT EXECUTE ON FUNCTION public.book_event(uuid, text, integer, text, date, text) TO authenticated;

CREATE OR REPLACE FUNCTION public.cancel_reservation(
  p_reservation_id uuid,
  p_user_key text
)
RETURNS json
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public
AS $$
DECLARE
  v_qty integer;
  v_event uuid;
BEGIN
  DELETE FROM public.reservations r
  WHERE r.id = p_reservation_id AND r.user_key = p_user_key
  RETURNING r.quantity, r.event_id INTO v_qty, v_event;

  IF NOT FOUND THEN
    RETURN json_build_object('ok', false, 'reason', 'not_found');
  END IF;

  UPDATE public.events
  SET tickets_reserved = tickets_reserved - v_qty
  WHERE id = v_event;

  RETURN json_build_object('ok', true);
END;
$$;

REVOKE ALL ON FUNCTION public.cancel_reservation(uuid, text) FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.cancel_reservation(uuid, text) TO anon;
GRANT EXECUTE ON FUNCTION public.cancel_reservation(uuid, text) TO authenticated;
