-- Optional one-off seed (matches in-app demo catalog). Run after migrations.
INSERT INTO public.events (title, iso_date, start_time, location, category, capacity, tickets_reserved)
VALUES
('Summer Concert 2026', '2026-03-15', '20:00', 'Bell Centre, Montreal', 'Concert', 500, 0),
('Tech Conference', '2026-03-20', '09:00', 'Palais des congrès, Montreal', 'Conference', 200, 0),
('Hockey Game', '2026-03-25', '19:00', 'Bell Centre, Montreal', 'Sports', 800, 0),
('Indie Film Night', '2026-04-02', '21:30', 'Cinéma du Parc, Montreal', 'Movie', 120, 0),
('Charlevoix Flavour Train Weekend', '2026-05-10', '08:15', 'Gare du Palais, Quebec City', 'Travel', 45, 0),
('Laurentian Shuttle & Spa Day', '2026-05-18', '07:45', 'Jean-Talon Metro, Montreal', 'Travel', 60, 0),
('Alouettes Preseason Scrimmage', '2026-04-12', '13:00', 'Percival Molson Stadium, Montreal', 'Sports', 350, 0),
('Montreal Jazz Evenings: Brass Session', '2026-04-28', '20:30', 'MTelus, Montreal', 'Concert', 280, 0),
('FinTech Canada Forum', '2026-05-05', '08:30', 'Fairmont Queen Elizabeth, Montreal', 'Conference', 150, 0),
('Dune Marathon IMAX', '2026-04-22', '18:45', 'Cinéma Banque Scotia, Montreal', 'Movie', 300, 0),
('Eastern Townships Winery Circuit', '2026-06-02', '09:00', 'Central Station, Montreal', 'Travel', 55, 0),
('PWHL Montreal Home Stand', '2026-03-30', '19:00', 'Place Bell, Laval', 'Sports', 420, 0),
('Osheaga Afterdark: Analog Dreams', '2026-07-14', '22:00', 'Parc Jean-Drapeau, Montreal', 'Concert', 600, 0),
('Design Systems at Scale', '2026-05-22', '10:00', 'Phi Centre, Montreal', 'Conference', 180, 0),
('National Canadian Film Day Encore', '2026-04-17', '18:30', 'Cinémathèque québécoise, Montreal', 'Movie', 90, 0);
