package com.example.ticket_reservation.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.example.ticket_reservation.database.model.Event;
import com.example.ticket_reservation.database.model.Registration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * Inserts deterministic mock data: 50 users, 120 events, 250 registrations.
 * <ul>
 *   <li>Only categories: Concert, Sports, Conference, Travel, Movie</li>
 *   <li>No registrations for cancelled events</li>
 *   <li>Full events: {@code registered_count == capacity}</li>
 *   <li>Uses fixed RNG seed for reproducible builds</li>
 * </ul>
 */
public final class TicketingDataSeeder {

    public static final int TARGET_USER_COUNT = 50;
    public static final int TARGET_EVENT_COUNT = 120;
    public static final int TARGET_REGISTRATION_COUNT = 250;

    private static final String[] CATEGORIES = {"Concert", "Sports", "Conference", "Travel", "Movie"};

    private static final String[] CITIES = {
            "Montreal", "Montreal", "Montreal", "Laval", "Longueuil", "Westmount",
            "Montreal", "Quebec City", "Ottawa", "Toronto", "Montreal", "Gatineau"
    };

    private static final java.util.Random R = new java.util.Random(42L);

    private TicketingDataSeeder() {
    }

    /**
     * Populates DB only if {@code events} is empty (safe for app startup).
     */
    public static void seedIfEmpty(@NonNull Context context) {
        TicketingOpenHelper helper = new TicketingOpenHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        seedIfEmpty(db);
        helper.close();
    }

    public static void seedIfEmpty(@NonNull SQLiteDatabase db) {
        if (countRows(db, TicketingContract.Events.TABLE) > 0) {
            return;
        }
        seed(db);
    }

    /**
     * Clears registrations/events/users and inserts full mock dataset.
     */
    public static void seed(@NonNull SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.delete(TicketingContract.Registrations.TABLE, null, null);
            db.delete(TicketingContract.Events.TABLE, null, null);
            db.delete(TicketingContract.Users.TABLE, null, null);

            long[] userIds = insertUsers(db);
            List<EventPlan> plans = buildEventPlans();
            long[] eventIds = insertEvents(db, plans);
            insertRegistrationsForPlans(db, userIds, eventIds, plans);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static int countRows(@NonNull SQLiteDatabase db, @NonNull String table) {
        try (Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + table, null)) {
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        }
        return 0;
    }

    private static long[] insertUsers(@NonNull SQLiteDatabase db) {
        long[] ids = new long[TARGET_USER_COUNT];
        for (int i = 0; i < TARGET_USER_COUNT; i++) {
            ContentValues v = new ContentValues();
            String first = FIRST_NAMES[i % FIRST_NAMES.length];
            String last = LAST_NAMES[(i * 7) % LAST_NAMES.length];
            v.put(TicketingContract.Users.FULL_NAME, first + " " + last);
            String email = (first + "." + last + i).toLowerCase(Locale.CANADA).replace(' ', '.') + "@mail.example";
            v.put(TicketingContract.Users.EMAIL, email);
            v.put(TicketingContract.Users.PHONE, phoneForIndex(i));
            long id = db.insert(TicketingContract.Users.TABLE, null, v);
            ids[i] = id;
        }
        return ids;
    }

    private static long[] insertEvents(@NonNull SQLiteDatabase db, @NonNull List<EventPlan> plans) {
        long[] ids = new long[plans.size()];
        for (int i = 0; i < plans.size(); i++) {
            EventPlan p = plans.get(i);
            ContentValues v = p.toContentValuesInitial();
            ids[i] = db.insert(TicketingContract.Events.TABLE, null, v);
        }
        return ids;
    }

    private static void insertRegistrationsForPlans(@NonNull SQLiteDatabase db,
                                                     @NonNull long[] userIds,
                                                     @NonNull long[] eventIds,
                                                     @NonNull List<EventPlan> plans) {
        String[] ticketTypes = {
                Registration.TICKET_STANDARD, Registration.TICKET_VIP, Registration.TICKET_STUDENT
        };
        String[] payments = {
                Registration.PAYMENT_PAID, Registration.PAYMENT_PAID, Registration.PAYMENT_PAID,
                Registration.PAYMENT_PENDING, Registration.PAYMENT_REFUNDED
        };

        for (int i = 0; i < plans.size(); i++) {
            EventPlan p = plans.get(i);
            long eid = eventIds[i];
            if (p.cancelled) {
                ContentValues cancelUp = new ContentValues();
                cancelUp.put(TicketingContract.Events.REGISTERED_COUNT, 0);
                cancelUp.put(TicketingContract.Events.STATUS, Event.STATUS_CANCELLED);
                db.update(TicketingContract.Events.TABLE, cancelUp,
                        TicketingContract.Events.ID + "=?", new String[]{String.valueOf(eid)});
                continue;
            }
            int n = p.targetRegistrationRows;
            for (int r = 0; r < n; r++) {
                ContentValues rv = new ContentValues();
                long uid = userIds[(i * 17 + r * 31) % userIds.length];
                rv.put(TicketingContract.Registrations.USER_ID, uid);
                rv.put(TicketingContract.Registrations.EVENT_ID, eid);
                rv.put(TicketingContract.Registrations.REGISTRATION_DATE, p.registrationDateForRow(r));
                rv.put(TicketingContract.Registrations.TICKET_TYPE, ticketTypes[(i + r) % ticketTypes.length]);
                rv.put(TicketingContract.Registrations.PAYMENT_STATUS, payments[(i * 3 + r) % payments.length]);
                db.insert(TicketingContract.Registrations.TABLE, null, rv);
            }

            ContentValues up = new ContentValues();
            up.put(TicketingContract.Events.REGISTERED_COUNT, n);
            up.put(TicketingContract.Events.STATUS, p.finalStatus());
            db.update(TicketingContract.Events.TABLE, up,
                    TicketingContract.Events.ID + "=?", new String[]{String.valueOf(eid)});
        }
    }

    private static List<EventPlan> buildEventPlans() {
        Set<Integer> cancelled = new HashSet<>();
        cancelled.add(3);
        cancelled.add(17);
        cancelled.add(31);
        cancelled.add(44);
        cancelled.add(58);
        cancelled.add(71);
        cancelled.add(94);
        cancelled.add(107);
        Set<Integer> fullSlots = pickDistinctIndices(6, cancelled, TARGET_EVENT_COUNT);
        Set<Integer> almostSlots = pickDistinctIndices(7, combine(cancelled, fullSlots), TARGET_EVENT_COUNT);

        List<EventPlan> out = new ArrayList<>(TARGET_EVENT_COUNT);
        for (int i = 0; i < TARGET_EVENT_COUNT; i++) {
            String category = CATEGORIES[i % CATEGORIES.length];
            int ordinal = i / CATEGORIES.length;
            EventPlan p = new EventPlan();
            p.index = i;
            p.category = category;
            p.title = buildTitle(category, ordinal);
            p.description = buildDescription(category, p.title);
            p.organizer = ORGANIZERS[(i * 11) % ORGANIZERS.length];
            p.venue = VENUES[(i * 13) % VENUES.length];
            p.city = CITIES[(i * 5) % CITIES.length];
            p.date = dateForEventIndex(i);
            p.startTime = String.format(Locale.US, "%02d:%02d", 17 + (i % 4), (i * 7) % 60);
            p.endTime = String.format(Locale.US, "%02d:%02d", 20 + (i % 3), (i * 11) % 60);
            p.imageName = String.format(Locale.US, "event_%s_%03d.webp",
                    category.toLowerCase(Locale.US), i + 1);
            p.contactEmail = "tickets." + category.toLowerCase(Locale.US) + (i % 50) + "@venue.example";

            if (cancelled.contains(i)) {
                p.cancelled = true;
                p.capacity = 40 + R.nextInt(120);
                p.targetRegistrationRows = 0;
                p.price = 0;
                p.free = false;
            } else if (fullSlots.contains(i)) {
                p.cancelled = false;
                p.capacity = 5 + R.nextInt(5);
                p.targetRegistrationRows = p.capacity;
                p.setPaidPricing(i);
            } else if (almostSlots.contains(i)) {
                p.cancelled = false;
                p.capacity = 12 + R.nextInt(7);
                p.targetRegistrationRows = Math.max(0, p.capacity - 2);
                p.setPaidPricing(i);
            } else {
                p.cancelled = false;
                p.capacity = 25 + R.nextInt(200);
                p.targetRegistrationRows = 0;
                p.setPaidOrFree(i);
            }
            out.add(p);
        }

        int sumRegs = 0;
        for (EventPlan p : out) {
            sumRegs += p.targetRegistrationRows;
        }
        int deficit = TARGET_REGISTRATION_COUNT - sumRegs;
        List<Integer> normal = new ArrayList<>();
        for (int i = 0; i < out.size(); i++) {
            EventPlan p = out.get(i);
            if (!p.cancelled && p.targetRegistrationRows < p.capacity && !fullSlots.contains(i)
                    && !almostSlots.contains(i)) {
                normal.add(i);
            }
        }
        Collections.shuffle(normal, R);
        int ni = 0;
        int stall = 0;
        while (deficit > 0 && !normal.isEmpty() && stall < normal.size() * 20) {
            int idx = normal.get(ni % normal.size());
            EventPlan p = out.get(idx);
            int room = p.capacity - p.targetRegistrationRows;
            if (room > 0) {
                int add = Math.min(room, Math.min(3, deficit));
                p.targetRegistrationRows += add;
                deficit -= add;
                stall = 0;
            } else {
                stall++;
            }
            ni++;
        }
        while (deficit > 0) {
            boolean progressed = false;
            for (int i = 0; i < out.size() && deficit > 0; i++) {
                EventPlan p = out.get(i);
                if (p.cancelled) {
                    continue;
                }
                int room = p.capacity - p.targetRegistrationRows;
                if (room > 0) {
                    p.targetRegistrationRows++;
                    deficit--;
                    progressed = true;
                }
            }
            if (!progressed) {
                break;
            }
        }

        return out;
    }

    @NonNull
    private static Set<Integer> combine(@NonNull Set<Integer> a, @NonNull Set<Integer> b) {
        Set<Integer> s = new HashSet<>(a);
        s.addAll(b);
        return s;
    }

    @NonNull
    private static Set<Integer> pickDistinctIndices(int count, @NonNull Set<Integer> forbidden, int max) {
        List<Integer> avail = new ArrayList<>();
        for (int i = 0; i < max; i++) {
            if (!forbidden.contains(i)) {
                avail.add(i);
            }
        }
        Collections.shuffle(avail, R);
        Set<Integer> s = new HashSet<>();
        int n = Math.min(count, avail.size());
        for (int i = 0; i < n; i++) {
            s.add(avail.get(i));
        }
        return s;
    }

    private static String dateForEventIndex(int i) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/Toronto"), Locale.CANADA);
        c.set(2026, Calendar.APRIL, 1, 12, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.add(Calendar.DAY_OF_MONTH, (i * 3 + (i % 7)) % 520);
        return String.format(Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));
    }

    private static String buildTitle(@NonNull String category, int ordinal) {
        switch (category) {
            case "Concert":
                return CONCERT_TITLES[ordinal % CONCERT_TITLES.length];
            case "Sports":
                return SPORTS_TITLES[ordinal % SPORTS_TITLES.length];
            case "Conference":
                return CONF_TITLES[ordinal % CONF_TITLES.length];
            case "Travel":
                return TRAVEL_TITLES[ordinal % TRAVEL_TITLES.length];
            case "Movie":
                return MOVIE_TITLES[ordinal % MOVIE_TITLES.length];
            default:
                return "Event " + ordinal;
        }
    }

    private static String buildDescription(@NonNull String category, @NonNull String title) {
        return "Join us for " + title + ". "
                + "Presented in the " + category.toLowerCase(Locale.US) + " series across Greater Montreal. "
                + "Doors open 60 minutes before start. Mobile tickets accepted.";
    }

    private static String phoneForIndex(int i) {
        int area = 514 + (i % 3) * 10;
        int mid = 200 + (i * 17) % 800;
        int line = 1000 + (i * 13) % 9000;
        return String.format(Locale.US, "(%03d) %03d-%04d", area, mid, line);
    }

    private static final class EventPlan {
        int index;
        String category;
        String title;
        String description;
        String organizer;
        String venue;
        String city;
        String date;
        String startTime;
        String endTime;
        String imageName;
        String contactEmail;
        boolean cancelled;
        int capacity;
        int targetRegistrationRows;
        double price;
        boolean free;

        void setPaidPricing(int seed) {
            price = 5 + (Math.abs((seed * 7919) % 296) + 1);
            if (price > 300) {
                price = 300;
            }
            free = false;
        }

        void setPaidOrFree(int seed) {
            if (seed % 7 == 0) {
                price = 0;
                free = true;
            } else {
                setPaidPricing(seed);
            }
        }

        @NonNull
        String finalStatus() {
            if (cancelled) {
                return Event.STATUS_CANCELLED;
            }
            if (targetRegistrationRows >= capacity && capacity > 0) {
                return Event.STATUS_FULL;
            }
            return Event.STATUS_UPCOMING;
        }

        @NonNull
        ContentValues toContentValuesInitial() {
            ContentValues v = new ContentValues();
            v.put(TicketingContract.Events.TITLE, title);
            v.put(TicketingContract.Events.DESCRIPTION, description);
            v.put(TicketingContract.Events.CATEGORY, category);
            v.put(TicketingContract.Events.ORGANIZER, organizer);
            v.put(TicketingContract.Events.VENUE, venue);
            v.put(TicketingContract.Events.CITY, city);
            v.put(TicketingContract.Events.DATE, date);
            v.put(TicketingContract.Events.START_TIME, startTime);
            v.put(TicketingContract.Events.END_TIME, endTime);
            v.put(TicketingContract.Events.CAPACITY, capacity);
            v.put(TicketingContract.Events.REGISTERED_COUNT, 0);
            v.put(TicketingContract.Events.PRICE, price);
            v.put(TicketingContract.Events.IS_FREE, free ? 1 : 0);
            v.put(TicketingContract.Events.STATUS, Event.STATUS_UPCOMING);
            v.put(TicketingContract.Events.IMAGE_NAME, imageName);
            v.put(TicketingContract.Events.CONTACT_EMAIL, contactEmail);
            return v;
        }

        @NonNull
        String registrationDateForRow(int row) {
            return date + String.format(Locale.US, " %02d:%02d:00", 10 + (row % 8), (index + row * 3) % 60);
        }
    }

    private static final String[] FIRST_NAMES = {
            "Amélie", "Marcus", "Priya", "Olivier", "Sofia", "Jean", "Aisha", "Thomas", "Camille", "Diego",
            "Léa", "Noah", "Fatima", "Étienne", "Maya", "Hugo", "Zainab", "Lucas", "Chloé", "Arjun",
            "Gabriel", "Nour", "Samuel", "Rachel", "Ibrahim", "Julien", "Emma", "Omar", "Clara", "Victor",
            "Sarah", "Alex", "Nadia", "William", "Ana", "David", "Yasmine", "James", "Elena", "Ryan",
            "Isabelle", "Kevin", "Layla", "Daniel", "Mia", "Chris", "Hannah", "Paul", "Zoe", "Simon"
    };

    private static final String[] LAST_NAMES = {
            "Tremblay", "Nguyen", "Roy", "Gagnon", "Côté", "Bouchard", "Morin", "Lavoie", "Fortin", "Gauthier",
            "Bergeron", "Leblanc", "Fontaine", "Pelletier", "Bélanger", "Lévesque", "Paquette", "Girard", "Simard", "Thibault",
            "Caron", "Beaulieu", "Cloutier", "Dubois", "Poirier", "Martel", "Richard", "Lefebvre", "Cormier", "Ouellette"
    };

    private static final String[] ORGANIZERS = {
            "Evenko Live", "Montreal Sports Group", "Palais des congrès Programming", "Voyage Québec Tours",
            "Cinéma du Parc Society", "Bell Centre Events", "McGill Conference Office", "Tourisme Montréal Partner Desk",
            "Osheaga Presents", "RDS Live Productions", "StartupMTL Collective", "VIA Rail Experiences"
    };

    private static final String[] VENUES = {
            "Bell Centre", "MTelus", "Place Bell", "Palais des congrès de Montréal", "Olympic Stadium",
            "Théâtre Maisonneuve", "Cinéma du Parc", "Phi Centre", "Fairmont Queen Elizabeth Ballroom",
            "Parc Jean-Drapeau Green Stage", "Centre Bell Practice Court", "Hôtel William Gray Rooftop"
    };

    private static final String[] CONCERT_TITLES = {
            "Osheaga Afterdark: Analog Dreams", "Montreal Jazz Evenings: Brass & Blue",
            "VELVET HALL: Synthwave Night", "Francofolies Club Session — Acoustic Lane",
            "METRO BEATS: Underground DJ Marathon", "Symphonique sous les étoiles",
            "Indie Québec Live: River City Session", "Northern Lights Folk Gathering",
            "R&B Montreal: Velvet Room Sessions", "Metalworks: Red Forge Tour Stop",
            "House on the Main: Extended Set", "Chamber Pop at Saint-James Church",
            "Afrobeats Montréal Block Party", "La Voix du Plateau — Chanson soirée",
            "Electronic Garden: Open Air Finale", "Blue Note Tribute: Hard Bop Revival",
            "Celtic Crossings Ceilidh", "K-Pop Night Market Live Stage",
            "Latin Groove: Salsa en el Puerto", "Piano Nocturne au Gesù",
            "Garage Rock Revival Tour", "Lo-Fi Study Session Live Band",
            "Gospel Rising: Union Chapel Choir", "Opera Pop-Up: Carmen Excerpts Outdoors"
    };

    private static final String[] SPORTS_TITLES = {
            "Alouettes Preseason: Red & Blue Scrimmage", "Canadiens Alumni Charity Skate",
            "CF Montréal vs Eastern Rivals", "Raptors 905 @ Montreal Showcase",
            "UFC Fight Night Montreal Qualifier", "Laval Rocket Weekend Doubleheader",
            "Canadian Grand Prix Fan Pit Walk", "Montreal Roller Derby Championship",
            "McGill vs Concordia Hockey Classic", "PWHL Montreal Home Stand",
            "Basketball Québec Cup Finals", "Tennis National Bank Open Qualifying Day",
            "Ironman 70.3 Spectator Pass", "Figure Skating Autumn Classic",
            "Lacrosse Nationals — Pool Play", "Rugby Canada Sevens Send-Off Match",
            "Marathon de Montréal Expo Day", "Ski Jumping Canada Cup (dryland)",
            "Boxing Gala: Centre Gervais", "Wrestling Supershow Laval",
            "College Football Northern Bowl", "Badminton Open — Longueuil",
            "Curling Trials Watch Party", "Speed Skating Oval Invitational"
    };

    private static final String[] CONF_TITLES = {
            "ConFoo Montreal 2026 — Platform Engineering", "AI Ethics & Policy Summit",
            "Design Systems at Scale (Workshop Day)", "FinTech Canada Regulatory Forum",
            "Northern DevOps Days", "Product Leadership Breakfast Series",
            "Cybersecurity Zero Trust Roadmap", "Sustainable Cities Research Colloquium",
            "Nursing Innovation & Patient Safety", "LegalTech Québec Bar Symposium",
            "Game Developers Meetup: Rendering", "Data Engineering on Lakehouse Architectures",
            "UX Research Methods Deep Dive", "Startup Funding Office Hours Marathon",
            "Cloud Native Kubernetes Patterns", "Biotech Venture Showcase",
            "Education Technology Fair", "Architecture & Urbanism Panel",
            "Supply Chain Resilience Workshop", "Marketing Analytics Live Labs",
            "Open Source Maintainer Summit", "Quantum Computing Readiness Briefing",
            "HR Tech: Hybrid Work Playbook", "Renewable Energy Policy Roundtable"
    };

    private static final String[] TRAVEL_TITLES = {
            "Charlevoix Flavour Train Weekend", "Eastern Townships Winery Circuit (guided)",
            "Saguenay Fjord Kayak & Lodge", "Ottawa Tulip Festival Coach Day",
            "Laurentian Maple Cabin Retreat", "Gaspésie Lighthouse Road Trip",
            "Toronto Weekend Express (hotel + show)", "Quebec City Winter Carnival Preview",
            "Mont-Tremblant Hiking & Spa Pass", "Îles-de-la-Madeleine Bike & Ferry",
            "Niagara Falls Lights Overnight", "Vermont Foliage Border Crossing Tour",
            "St. Lawrence River Cruise — Dinner", "Indigenous-Led Cultural Walk (Kahnawà:ke)",
            "Sugar Shack Season Family Bus", "Northern Lights Chase (Churchill charter)",
            "Prince Edward County Tasting Trail", "Newfoundland Iceberg Alley Fly-In",
            "Banff Photography Intensive (YYZ hub)", "Pacific Coastal Hop — Vancouver Island",
            "Cabot Trail Motorcycle Convoy", "Fundy Tides Eco Tour",
            "Halifax Waterfront Music Weekend", "Rockies Rail & Hike Combo"
    };

    private static final String[] MOVIE_TITLES = {
            "Dune: Part Three — IMAX 70mm Premiere", "Midnight Anime Classics: Akira",
            "Cannes Winners Spotlight — Single Screen", "Quebecois Documentary Double Feature",
            "Studio Ghibli Brunch Screening", "Horror Retro: The Thing (1982) 4K",
            "Silent Film with Live Organ", "Marvel Midnight Fan Event",
            "Criterion Collection: Wong Kar-wai", "Family Matinee: Pixar Shorts + Feature",
            "Drive-In Revival: Grease Sing-Along", "National Canadian Film Day — Encore",
            "Sci-Fi Marathon: 2001 + Interstellar", "Bollywood Blockbuster Night",
            "Oscar Shorts Package — Animation", "Film Noir at the Rialto",
            "VR Cinema Lab Experiments", "Outdoor Screening: La Grande Séduction",
            "Director Q&A: Indie Thriller", "Festivals des cinémas arabes — Opening",
            "LGBTQ+ Pride Film Series", "Environmental Film Festival Selection",
            "Sports Doc: Hoop Dreams Remastered", "Holiday Classic: It's a Wonderful Life"
    };
}
