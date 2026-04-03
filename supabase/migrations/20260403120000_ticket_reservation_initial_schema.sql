-- Events + reservations (Postgres / Supabase). Apply before RPC migration.
-- Policies use DROP IF EXISTS so this script can be re-run safely in the SQL editor.

CREATE TABLE IF NOT EXISTS public.events (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  title text NOT NULL,
  iso_date date NOT NULL,
  location text NOT NULL,
  category text NOT NULL,
  canceled boolean NOT NULL DEFAULT false,
  capacity integer NOT NULL CHECK (capacity >= 0),
  tickets_reserved integer NOT NULL DEFAULT 0 CHECK (tickets_reserved >= 0),
  created_at timestamptz NOT NULL DEFAULT now(),
  CONSTRAINT tickets_reserved_lte_capacity CHECK (tickets_reserved <= capacity)
);

CREATE TABLE IF NOT EXISTS public.reservations (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  event_id uuid NOT NULL REFERENCES public.events(id) ON DELETE CASCADE,
  user_key text NOT NULL,
  quantity integer NOT NULL CHECK (quantity > 0),
  event_title_snapshot text NOT NULL,
  event_iso_date_snapshot date NOT NULL,
  event_location_snapshot text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_reservations_user_key ON public.reservations(user_key);
CREATE INDEX IF NOT EXISTS idx_reservations_event_id ON public.reservations(event_id);

ALTER TABLE public.events ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reservations ENABLE ROW LEVEL SECURITY;

-- Events policies
DROP POLICY IF EXISTS "events_select_anon" ON public.events;
DROP POLICY IF EXISTS "events_update_anon" ON public.events;
DROP POLICY IF EXISTS "events_insert_anon" ON public.events;
DROP POLICY IF EXISTS "events_delete_anon" ON public.events;

DROP POLICY IF EXISTS "reservations_select_anon" ON public.reservations;
DROP POLICY IF EXISTS "reservations_insert_anon" ON public.reservations;
DROP POLICY IF EXISTS "reservations_delete_anon" ON public.reservations;
DROP POLICY IF EXISTS "events_all_authenticated" ON public.events;
DROP POLICY IF EXISTS "reservations_all_authenticated" ON public.reservations;

CREATE POLICY "events_select_anon" ON public.events FOR SELECT TO anon USING (true);
CREATE POLICY "events_update_anon" ON public.events FOR UPDATE TO anon USING (true) WITH CHECK (true);
CREATE POLICY "events_insert_anon" ON public.events FOR INSERT TO anon WITH CHECK (true);
CREATE POLICY "events_delete_anon" ON public.events FOR DELETE TO anon USING (true);

CREATE POLICY "reservations_select_anon" ON public.reservations FOR SELECT TO anon USING (true);
CREATE POLICY "reservations_insert_anon" ON public.reservations FOR INSERT TO anon WITH CHECK (true);
CREATE POLICY "reservations_delete_anon" ON public.reservations FOR DELETE TO anon USING (true);

CREATE POLICY "events_all_authenticated" ON public.events FOR ALL TO authenticated USING (true) WITH CHECK (true);
CREATE POLICY "reservations_all_authenticated" ON public.reservations FOR ALL TO authenticated USING (true) WITH CHECK (true);
