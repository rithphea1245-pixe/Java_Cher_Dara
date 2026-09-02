DROP TABLE IF EXISTS wow_scores CASCADE;
DROP TABLE IF EXISTS wow_levels CASCADE;
DROP TABLE IF EXISTS player_progress CASCADE;
DROP TABLE IF EXISTS questions CASCADE;
DROP TABLE IF EXISTS levels CASCADE;
DROP TABLE IF EXISTS worlds CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    total_points INT NOT NULL DEFAULT 0,
    is_admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS worlds (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS levels (
    id SERIAL PRIMARY KEY,
    world_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    point_reward INT NOT NULL DEFAULT 0,
    FOREIGN KEY (world_id) REFERENCES worlds(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS questions (
    id SERIAL PRIMARY KEY,
    level_id INT NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer VARCHAR(10) NOT NULL,
    hint TEXT,
    FOREIGN KEY (level_id) REFERENCES levels(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS player_progress (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    level_id INT NOT NULL,
    correct_answers INT NOT NULL DEFAULT 0,
    total_questions INT NOT NULL DEFAULT 0,
    points_earned INT NOT NULL DEFAULT 0,
    completed SMALLINT NOT NULL DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (level_id) REFERENCES levels(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS wow_levels (
    id SERIAL PRIMARY KEY,
    world_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    theme VARCHAR(100) NOT NULL,
    words TEXT NOT NULL,
    point_reward INT NOT NULL DEFAULT 0,
    FOREIGN KEY (world_id) REFERENCES worlds(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS wow_scores (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    wow_level_id INT NOT NULL,
    words_found INT NOT NULL DEFAULT 0,
    total_words INT NOT NULL DEFAULT 0,
    points_earned INT NOT NULL DEFAULT 0,
    hints_used INT NOT NULL DEFAULT 0,
    completed SMALLINT NOT NULL DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (wow_level_id) REFERENCES wow_levels(id) ON DELETE CASCADE
);

INSERT INTO users (id, username, email, password, total_points, is_admin) VALUES
    (1, 'admin', 'admin@example.com', 'admin67', 30, TRUE),
    (2, 'bob', 'bob@example.com', 'password456', 10, FALSE);

INSERT INTO worlds (id, name, description) VALUES
    (1, 'Ancient Egypt', 'Explore the mysteries of the pharaohs and the pyramids.'),
    (2, 'Outer Space', 'Journey through the cosmos and uncover the secrets of the universe.'),
    (3, 'The Deep Ocean', 'Dive into the abyss and discover the wonders beneath the waves.'),
    (4, 'Dinosaur World', 'Travel back in time to walk with the giants of the prehistoric era.'),
    (5, 'Medieval Kingdoms', 'Enter the age of castles, knights, and legendary quests.'),
    (6, 'Rainforest Adventure', 'Venture into the lush jungle and meet its amazing creatures.');

INSERT INTO levels (id, world_id, name, difficulty, point_reward) VALUES
    (1, 1, 'Pyramids of Giza', 'easy', 10),
    (2, 1, 'Secrets of the Nile', 'medium', 15),
    (3, 1, 'Valley of the Kings', 'hard', 20),
    (4, 2, 'Planets', 'easy', 10),
    (5, 2, 'Stellar Nurseries', 'medium', 15),
    (6, 2, 'Black Holes', 'hard', 20),
    (7, 3, 'Coral Reefs', 'easy', 10),
    (8, 3, 'Ocean Giants', 'medium', 15),
    (9, 3, 'The Deep Trench', 'hard', 20),
    (10, 4, 'Early Discoveries', 'easy', 10),
    (11, 4, 'Mesozoic Era', 'medium', 15),
    (12, 4, 'Mass Extinction', 'hard', 20),
    (13, 5, 'Castles', 'easy', 10),
    (14, 5, 'Knights', 'medium', 15),
    (15, 5, 'Medieval Life', 'hard', 20),
    (16, 6, 'Canopy Explorers', 'easy', 10),
    (17, 6, 'Jungle Creatures', 'medium', 15),
    (18, 6, 'Survival Skills', 'hard', 20);

INSERT INTO questions (id, level_id, question_text, option_a, option_b, option_c, option_d, correct_answer, hint) VALUES
    (1, 1, 'How many Great Pyramids stand on the Giza Plateau?', 'One', 'Two', 'Three', 'Four', 'C', 'They are named after three pharaohs.'),
    (2, 1, 'Which pharaoh is credited with building the Great Pyramid?', 'Khufu', 'Ramses II', 'Tutankhamun', 'Cleopatra', 'A', 'He was the second ruler of the 4th dynasty.'),
    (3, 2, 'In which direction does the Nile River flow?', 'North', 'South', 'East', 'West', 'A', 'It runs toward the Mediterranean Sea.'),
    (4, 2, 'What was papyrus primarily used for in Ancient Egypt?', 'Weapons', 'Writing material', 'Building blocks', 'Jewelry', 'B', 'The English word "paper" is derived from it.'),
    (5, 3, 'Which famous pharaoh tomb was discovered almost completely intact in 1922 by Howard Carter?', 'Ramses II', 'Tutankhamun', 'Akhenaten', 'Thutmose III', 'B', 'He became king at a very young age.'),
    (6, 4, 'Which planet is known as the Red Planet?', 'Venus', 'Mars', 'Jupiter', 'Saturn', 'B', 'It is named after the Roman god of war.'),
    (7, 4, 'Which is the largest planet in our solar system?', 'Earth', 'Neptune', 'Jupiter', 'Mars', 'C', 'It is famous for its Great Red Spot.'),
    (8, 5, 'What is a stellar nursery primarily composed of?', 'Solid rock', 'Gas and dust', 'Liquid hydrogen', 'Metallic iron', 'B', 'New stars are born inside these giant clouds.'),
    (9, 6, 'What is the boundary around a black hole called?', 'Event horizon', 'Accretion disk', 'Singularity', 'Photon ring', 'A', 'Nothing, not even light, can escape from inside it.'),
    (10, 6, 'What radiation did Stephen Hawking show black holes emit?', 'Light', 'Hawking radiation', 'Sound', 'Neutrinos', 'B', 'It is named after the physicist himself.'),
    (11, 7, 'What is the largest coral reef system in the world?', 'Great Barrier Reef', 'Red Sea Reef', 'Amazon Reef', 'Belize Barrier Reef', 'A', 'It lies off the coast of Australia.'),
    (12, 7, 'What type of tiny animal builds coral reefs?', 'Mollusks', 'Polyps', 'Crustaceans', 'Sponges', 'B', 'Small creatures with stinging tentacles.'),
    (13, 8, 'Which is the largest animal ever known to have lived?', 'Great White Shark', 'Blue Whale', 'Orca', 'Elephant', 'B', 'A whale that can grow over 30 meters long.'),
    (14, 8, 'Which is the largest fish in the ocean?', 'Blue Whale', 'Great White Shark', 'Whale Shark', 'Manta Ray', 'C', 'A gentle giant covered in pale spots.'),
    (15, 9, 'What is the deepest part of the world''s oceans?', 'Mariana Trench', 'Java Trench', 'Tonga Trench', 'Puerto Rico Trench', 'A', 'It lies in the western Pacific Ocean.'),
    (16, 9, 'Which ocean zone receives no sunlight at all?', 'Sunlight Zone', 'Twilight Zone', 'Midnight Zone', 'Sunlit Zone', 'C', 'It is also called the bathypelagic zone.'),
    (17, 10, 'What does the word "dinosaur" mean?', 'Terrible lizard', 'Ancient reptile', 'Giant beast', 'Prehistoric bird', 'A', 'It comes from Greek words meaning "terrible lizard".'),
    (18, 10, 'Which era is known as the Age of Dinosaurs?', 'Paleozoic', 'Mesozoic', 'Cenozoic', 'Precambrian', 'B', 'It includes the Triassic, Jurassic, and Cretaceous periods.'),
    (19, 11, 'Which meat-eating dinosaur is famous for its tiny arms?', 'Stegosaurus', 'Triceratops', 'Tyrannosaurus rex', 'Brachiosaurus', 'C', 'Its name means "tyrant lizard king".'),
    (20, 11, 'Which dinosaur was a long-necked plant-eater?', 'Velociraptor', 'Brachiosaurus', 'Allosaurus', 'Pteranodon', 'B', 'One of the largest land animals ever.'),
    (21, 12, 'What is the most widely accepted cause of the dinosaur extinction?', 'Asteroid impact', 'Ice age', 'Widespread disease', 'Sudden cold snap', 'A', 'A giant space rock struck near Mexico 66 million years ago.'),
    (22, 12, 'Which period marks the end of the dinosaurs?', 'Triassic', 'Jurassic', 'Cretaceous', 'Permian', 'C', 'It ends with the K-Pg extinction event.'),
    (23, 13, 'What was the tall central tower of a castle called?', 'Keep', 'Dungeon', 'Turret', 'Barbican', 'A', 'The strongest and most protected part of the castle.'),
    (24, 13, 'What is the water-filled ditch around a castle called?', 'Rampart', 'Moat', 'Drawbridge', 'Parapet', 'B', 'It made it harder for attackers to reach the walls.'),
    (25, 14, 'What was the ceremony called in which a man became a knight?', 'Dubbing', 'Consecration', 'Coronation', 'Baptism', 'A', 'A sword was tapped on each shoulder.'),
    (26, 14, 'What did knights wear for protection in battle?', 'Chain mail', 'Leather coats', 'Helmets only', 'Shields only', 'A', 'Armor made of interlocking metal rings.'),
    (27, 15, 'What large gathering brought traders and entertainers to medieval towns?', 'Tournament', 'Fair', 'Feast', 'Pilgrimage', 'B', 'A regular market where people bought and sold goods.'),
    (28, 15, 'What was the open space in the center of a medieval town called?', 'Castle yard', 'Market square', 'Monastery', 'Guild hall', 'B', 'The heart of the town where trading took place.'),
    (29, 16, 'What is the leafy top layer of the rainforest called?', 'Understory', 'Forest floor', 'Canopy', 'Emergent layer', 'C', 'The roof formed by the branches of tall trees.'),
    (30, 16, 'Which rainforest layer receives the most sunlight?', 'Forest floor', 'Understory', 'Canopy', 'Emergent', 'D', 'The tallest trees that rise above the canopy.'),
    (31, 17, 'What is the largest rainforest in the world?', 'Congo Basin', 'Amazon', 'Daintree', 'Sundarbans', 'B', 'It spans across South America.'),
    (32, 17, 'Which bird is famous for its large, colorful beak?', 'Macaw', 'Toucan', 'Hummingbird', 'Falcon', 'B', 'Its beak can be bigger than its head.'),
    (33, 18, 'What should you do if you get lost in a rainforest?', 'Run in one direction', 'Stay put and signal for help', 'Follow a river downstream', 'Climb the tallest tree', 'B', 'Rescuers find moving people harder to locate.'),
    (34, 18, 'Which plant can provide a safe drink of water in a rainforest?', 'Bamboo', 'Poison ivy', 'Venus flytrap', 'Cactus', 'A', 'Its hollow stems can hold clean water.');

INSERT INTO questions (id, level_id, question_text, option_a, option_b, option_c, option_d, correct_answer, hint) VALUES
    (35, 7, 'What covers most of the Earth''s surface?', 'Ocean', 'Desert', 'Forest', 'Ice', 'A', 'About 71 percent of Earth is covered by water.'),
    (36, 7, 'How many major oceans are there?', 'Three', 'Five', 'Seven', 'Nine', 'B', 'Pacific, Atlantic, Indian, Southern, and Arctic.'),
    (37, 7, 'What is the largest ocean on Earth?', 'Atlantic', 'Indian', 'Pacific', 'Arctic', 'C', 'It covers more than a third of the planet.'),
    (38, 7, 'What color is a healthy coral reef?', 'White', 'Red', 'Gray', 'Colorful', 'D', 'Healthy corals are home to colorful algae.'),
    (39, 7, 'What do coral reefs provide for fish?', 'Food and shelter', 'Sunlight', 'Fresh water', 'Strong wind', 'A', 'Reefs are home to about a quarter of sea life.'),
    (40, 7, 'Which ocean animal has eight arms?', 'Starfish', 'Octopus', 'Jellyfish', 'Seahorse', 'B', 'Octopus means eight-footed.'),
    (41, 7, 'Which of these ocean animals is a fish?', 'Dolphin', 'Whale', 'Shark', 'Turtle', 'C', 'Sharks breathe with gills like all fish.'),
    (42, 7, 'What does a whale use to breathe air?', 'Gills', 'Blowhole', 'Fins', 'Tail', 'B', 'Whales must rise to the surface to breathe.'),
    (43, 7, 'What is a group of fish swimming together called?', 'Flock', 'Herd', 'School', 'Pack', 'C', 'Fish swim in schools to stay safe.'),
    (44, 7, 'Which beach animal has a hard shell and pincers?', 'Crab', 'Whale', 'Octopus', 'Shark', 'A', 'Crabs scuttle sideways on sandy beaches.'),
    (45, 7, 'What usually causes ocean waves?', 'Wind', 'Fish', 'Sand', 'Caves', 'A', 'Wind pushes the water into waves.'),
    (46, 7, 'Which sea creature can glow in the dark?', 'Jellyfish', 'Clownfish', 'Tuna', 'Salmon', 'A', 'Many jellyfish produce their own light.'),
    (47, 7, 'What is a baby whale called?', 'Pup', 'Calf', 'Cub', 'Kit', 'B', 'A young whale is known as a calf.'),
    (48, 7, 'Which fish hides among sea anemones?', 'Clownfish', 'Shark', 'Eel', 'Tuna', 'A', 'Clownfish are safe from the anemone sting.'),
    (49, 7, 'What do sea turtles mostly eat?', 'Seaweed and jellyfish', 'Rocks', 'Sand', 'Plastic', 'A', 'Most sea turtles graze on sea plants or jellyfish.'),
    (50, 7, 'What is the highest point of a wave called?', 'Crest', 'Trough', 'Base', 'Peak', 'A', 'The crest is the top of a wave.'),
    (51, 7, 'What kind of animal is a starfish?', 'Fish', 'Crustacean', 'Echinoderm', 'Mollusk', 'C', 'Starfish are related to sea urchins.'),
    (52, 7, 'What do we call animals that live only in the sea?', 'Terrestrial', 'Marine', 'Aerial', 'Desert', 'B', 'Marine means living in the ocean.'),
    (53, 7, 'Which of these animals lives on a coral reef?', 'Pufferfish', 'Penguin', 'Camel', 'Fox', 'A', 'Pufferfish live among coral reefs.'),
    (54, 7, 'What is the sandy area along the ocean called?', 'Beach', 'Forest', 'Mountain', 'Cave', 'A', 'Beaches are made of sand and pebbles.'),
    (55, 7, 'Which fish has a long sword-like snout?', 'Swordfish', 'Goldfish', 'Catfish', 'Jellyfish', 'A', 'The swordfish uses its snout to stun prey.'),
    (56, 7, 'What do fish use to swim through water?', 'Legs', 'Wings', 'Fins', 'Arms', 'C', 'Fins steer and push fish forward.'),
    (57, 7, 'What is the study of the ocean called?', 'Oceanography', 'Astronomy', 'Geology', 'Botany', 'A', 'Oceanography is the study of oceans.'),
    (58, 7, 'Which animal travels thousands of miles across the ocean?', 'Whale', 'Starfish', 'Sea urchin', 'Clam', 'A', 'Whales migrate to feed and breed.'),
    (59, 7, 'What is the white foam you see at the beach?', 'Sea foam', 'Snow', 'Ice', 'Sand', 'A', 'Sea foam forms from stirred-up ocean matter.'),
    (60, 7, 'Which sea creature stings with its tentacles?', 'Jellyfish', 'Shark', 'Tuna', 'Crab', 'A', 'Jellyfish stun prey with stinging cells.'),
    (61, 7, 'What do we call the rising and falling of the sea?', 'Tide', 'Current', 'Storm', 'Ripple', 'A', 'Tides are caused by the Moon''s gravity.'),
    (62, 7, 'Which of these is NOT an ocean animal?', 'Dolphin', 'Seal', 'Eagle', 'Octopus', 'C', 'Eagles build nests high in the sky.'),
    (147, 8, 'What is the largest animal ever known to have lived?', 'Great White Shark', 'Blue Whale', 'Orca', 'Elephant', 'B', 'Blue whales reach over 30 meters long.'),
    (148, 8, 'How much can a blue whale weigh?', 'About 10 tons', 'About 40 tons', 'About 200 tons', 'About 1 ton', 'C', 'The blue whale can weigh as much as 200 tons.'),
    (149, 8, 'What is the fastest fish in the ocean?', 'Sailfish', 'Tuna', 'Mako Shark', 'Swordfish', 'A', 'Sailfish can swim over 100 km per hour.'),
    (150, 8, 'Which land animal is the closest living relative of whales?', 'Hippopotamus', 'Elephant', 'Lion', 'Bear', 'A', 'DNA shows whales are related to hippos.'),
    (151, 8, 'How do baleen whales feed?', 'By straining water', 'By biting', 'By sucking sand', 'By clawing rocks', 'A', 'Baleen plates filter food from the water.'),
    (152, 8, 'What do orcas hunt?', 'Fish and seals', 'Only plankton', 'Seaweed', 'Sand', 'A', 'Orcas are top ocean predators.'),
    (153, 8, 'Which whale is famous for singing long songs?', 'Humpback whale', 'Blue whale', 'Sperm whale', 'Beluga', 'A', 'Male humpbacks sing the longest songs.'),
    (154, 8, 'What is the largest shark in the ocean?', 'Great White Shark', 'Whale Shark', 'Hammerhead', 'Tiger Shark', 'B', 'Whale sharks can reach 18 meters.'),
    (155, 8, 'What special sense do sharks use to find prey?', 'Electroreception', 'X-ray vision', 'Taste in their fins', 'Hearing with bones', 'A', 'They sense the electric fields of other animals.'),
    (156, 8, 'How long can a sperm whale stay underwater?', 'A few seconds', 'Over an hour', 'A whole day', 'Almost a week', 'B', 'Sperm whales make long, deep dives.'),
    (157, 8, 'Which deep-sea giant has the largest eyes?', 'Giant squid', 'Blue whale', 'Hammerhead shark', 'Dolphin', 'A', 'Giant squid eyes can be 25 centimeters wide.'),
    (158, 8, 'What do manatees eat?', 'Seagrass', 'Fish', 'Crabs', 'Jellyfish', 'A', 'Manatees graze peacefully on sea plants.'),
    (159, 8, 'Which whale actually belongs to the dolphin family?', 'Orca', 'Blue whale', 'Sperm whale', 'Fin whale', 'A', 'Orcas are the largest dolphins.'),
    (160, 8, 'What is the largest animal without a backbone?', 'Giant squid', 'Blue whale', 'Octopus', 'Giant crab', 'A', 'Giant squid are the largest invertebrates.'),
    (161, 8, 'How do whales communicate across long distances?', 'With sound', 'With smell', 'With flashes of light', 'By touch', 'A', 'Whale songs travel far underwater.'),
    (162, 8, 'What is the top predator of the Arctic Ocean?', 'Polar bear', 'Penguin', 'Bowhead whale', 'Walrus', 'A', 'Polar bears hunt seals on the ice.'),
    (163, 8, 'What do manta rays filter from the water?', 'Plankton', 'Small sharks', 'Seaweed', 'Sand', 'A', 'Manta rays feed on tiny plankton.'),
    (164, 8, 'Which whale has a long spiraled tusk?', 'Narwhal', 'Dolphin', 'Seal', 'Sea lion', 'A', 'The narwhal''s tusk is an overgrown tooth.'),
    (219, 9, 'How deep is the Mariana Trench?', 'About 2,000 meters', 'About 5,000 meters', 'About 11,000 meters', 'About 20,000 meters', 'C', 'It is nearly 11 kilometers deep.'),
    (220, 9, 'What is the deepest known point on Earth called?', 'Challenger Deep', 'The Abyss', 'Ocean Floor', 'Hadal Plain', 'A', 'Challenger Deep sits in the Mariana Trench.'),
    (221, 9, 'What is the water pressure at the bottom of the trench?', 'Over 1,000 times normal', 'The same as the surface', 'Nearly zero', 'About ten times normal', 'A', 'The crushing pressure is extreme down there.'),
    (222, 9, 'What do we call organisms that produce their own light?', 'Bioluminescent', 'Photosynthetic', 'Symbiotic', 'Parasitic', 'A', 'Deep-sea life uses bioluminescence.'),
    (223, 9, 'Which deep-sea fish attracts prey with a glowing lure?', 'Anglerfish', 'Clownfish', 'Pufferfish', 'Lionfish', 'A', 'The anglerfish dangles a glowing lure.'),
    (224, 9, 'Where is the hadal zone found?', 'Deeper than 6,000 meters', 'At the shoreline', 'In the polar seas', 'Just below the surface', 'A', 'Hadal zones are the very deepest parts of the sea.'),
    (225, 9, 'What heats the water around deep-sea vents?', 'Hydrothermal vents', 'Geysers', 'Sunlight', 'Underwater lava lakes', 'A', 'Hydrothermal vents release superheated water.'),
    (226, 9, 'What do bacteria near hydrothermal vents use for energy?', 'Hydrogen sulfide', 'Pure water', 'Sunlight', 'Salt only', 'A', 'Vent bacteria live by chemosynthesis.');

INSERT INTO questions (id, level_id, question_text, option_a, option_b, option_c, option_d, correct_answer, hint) VALUES
    (63, 10, 'How long ago did dinosaurs first appear?', 'About 230 million years ago', 'About 2,000 years ago', 'About 100 years ago', 'About 5 million years ago', 'A', 'Dinosaurs ruled for over 150 million years.'),
    (64, 10, 'What do we call the remains of ancient life preserved in rock?', 'Minerals', 'Fossils', 'Crystals', 'Comets', 'B', 'Fossils can be bones, teeth, or footprints.'),
    (65, 10, 'Which dinosaur had three horns on its head?', 'Tyrannosaurus', 'Velociraptor', 'Triceratops', 'Stegosaurus', 'C', 'Its name means three-horned face.'),
    (66, 10, 'Which dinosaur is known for the plates on its back?', 'Allosaurus', 'Parasaurolophus', 'Triceratops', 'Stegosaurus', 'D', 'The plates run down its back to its tail.'),
    (67, 10, 'What did plant-eating dinosaurs eat?', 'Plants and leaves', 'Meat and fish', 'Rocks and sand', 'Ice and snow', 'A', 'Plant-eaters are called herbivores.'),
    (68, 10, 'What did meat-eating dinosaurs eat?', 'Leaves', 'Other animals', 'Grass', 'Flowers', 'B', 'Meat-eaters are called carnivores.'),
    (69, 10, 'Did all dinosaurs live in the sea?', 'Yes, all of them', 'Only the babies', 'No, they lived on land', 'No one knows', 'C', 'Dinosaurs were land animals.'),
    (70, 10, 'What came out of dinosaur eggs?', 'Fish', 'Birds', 'Insects', 'Baby dinosaurs', 'D', 'All dinosaurs hatched from eggs.'),
    (71, 10, 'Which dinosaur name means king tyrant lizard?', 'Tyrannosaurus rex', 'Stegosaurus', 'Apatosaurus', 'Triceratops', 'A', 'T. rex was one of the biggest meat-eaters.'),
    (72, 10, 'Were all dinosaurs enormous giants?', 'Yes, all were giants', 'No, some were very small', 'They were all one size', 'Only adults were small', 'B', 'Some were no bigger than a chicken.'),
    (73, 10, 'Which flying reptile lived at the same time as dinosaurs?', 'Kite', 'Dragon', 'Pterodactyl', 'Ancient bee', 'C', 'Pterodactyls flew on wings of skin.'),
    (74, 10, 'What is the study of prehistoric life called?', 'Astronomy', 'Oceanography', 'Meteorology', 'Paleontology', 'D', 'Paleontologists dig up fossils.'),
    (75, 10, 'How do scientists learn what dinosaurs ate?', 'From their teeth and fossils', 'By watching them', 'From old stories', 'From paintings', 'A', 'Sharp teeth mean meat-eaters.'),
    (76, 10, 'Which small dinosaur was a fast hunter?', 'Brachiosaurus', 'Velociraptor', 'Stegosaurus', 'Ankylosaurus', 'B', 'It ran on two legs and had big claws.'),
    (77, 10, 'Which dinosaur had a heavy club on its tail?', 'Triceratops', 'Pterodactyl', 'Ankylosaurus', 'Brontosaurus', 'C', 'The club could smash attackers.'),
    (78, 10, 'What were the biggest dinosaurs called?', 'Theropods', 'Ornithopods', 'Ankylosaurs', 'Sauropods', 'D', 'Sauropods had long necks and giant bodies.'),
    (79, 10, 'Where have most dinosaur fossils been found?', 'All around the world', 'In only one country', 'Only in the ocean', 'Only in caves', 'A', 'Fossils appear on every continent.'),
    (80, 10, 'Which dinosaur had a big bony frill around its neck?', 'Velociraptor', 'Triceratops', 'Tyrannosaurus', 'Iguanodon', 'B', 'The frill helped protect its neck.'),
    (81, 10, 'Did many dinosaurs build nests for their eggs?', 'No, never', 'Yes, they did', 'Only sea reptiles', 'Baby dinosaurs only', 'B', 'Fossil nests have been found with eggs.'),
    (82, 10, 'What color were dinosaurs?', 'Always green', 'Always gray', 'We do not know for sure', 'Always red', 'C', 'Skin rarely survives as a fossil.'),
    (83, 10, 'Which animals alive today are the closest relatives of dinosaurs?', 'Birds', 'Sharks', 'Cats', 'Elephants', 'A', 'Birds evolved from small dinosaurs.'),
    (84, 10, 'How did most dinosaurs move?', 'Swimming', 'By walking or running', 'Flying', 'Jumping', 'B', 'Most walked or ran on legs.'),
    (85, 10, 'What did the smallest dinosaurs often eat?', 'Whole trees', 'Only fish', 'Insects and seeds', 'Only bones', 'C', 'Small jaws ate small food.'),
    (86, 10, 'What is a full set of fossil bones called?', 'A volcano', 'A canyon', 'A crater', 'A skeleton', 'D', 'Skeletons show how an animal stood.'),
    (87, 10, 'Which dinosaur used sharp spikes on its tail for defense?', 'Stegosaurus', 'Triceratops', 'Tyrannosaurus', 'Velociraptor', 'A', 'Its tail spikes pointed outward.'),
    (88, 10, 'Did humans ever live at the same time as dinosaurs?', 'Yes, together', 'Only in caves', 'No, dinosaurs lived much earlier', 'No one knows', 'C', 'Dinosaurs died out 66 million years ago.'),
    (89, 10, 'What do scientists use to tell the age of dinosaur fossils?', 'Guessing', 'Star charts', 'Magic', 'Rock layers and dating methods', 'D', 'Deeper rocks are usually older.'),
    (90, 10, 'Which word means an animal that eats meat?', 'Herbivore', 'Omnivore', 'Invertebrate', 'Carnivore', 'D', 'Carnivores hunt other animals.'),
    (165, 11, 'Which dinosaur had a giant sail on its back?', 'Spinosaurus', 'Velociraptor', 'Iguanodon', 'Triceratops', 'A', 'The sail may have kept it warm or cool.'),
    (166, 11, 'What evidence shows that some dinosaurs lived in herds?', 'Old paintings', 'Fossil trackways and bonebeds', 'Ancient books', 'Carved stones', 'B', 'Many footprints in the same line suggest a herd.'),
    (167, 11, 'Why did T. rex have such short arms?', 'No one knows for sure, many ideas exist', 'To fly', 'To dig tunnels', 'To hold babies', 'A', 'Scientists debate the arms'' use.'),
    (168, 11, 'What are coprolites?', 'Fossilized dinosaur droppings', 'Dinosaur eggs', 'Dinosaur teeth', 'Dinosaur nests', 'A', 'They can show what an animal ate.'),
    (169, 11, 'Which dinosaur had a dome of thick bone on its skull?', 'Parasaurolophus', 'Pachycephalosaurus', 'Ankylosaurus', 'Allosaurus', 'B', 'It may have butted rivals with its dome.'),
    (170, 11, 'What may Stegosaurus plates have been used for?', 'Flying', 'To control heat or show off', 'Digging', 'Swimming', 'B', 'They were too thin for armor.'),
    (171, 11, 'Which ocean reptiles lived alongside dinosaurs?', 'Plesiosaurs and ichthyosaurs', 'Pterodactyls', 'Mammoths', 'Giant sloths', 'A', 'They swam in ancient seas.'),
    (172, 11, 'What do growth rings in dinosaur bones tell us?', 'What it ate', 'Where it lived', 'How old the animal was', 'Its color', 'C', 'Rings build up each year, like a tree''s.'),
    (173, 11, 'Which dinosaur could have up to 500 teeth?', 'Tyrannosaurus', 'Stegosaurus', 'Brachiosaurus', 'Nigersaurus', 'D', 'It kept growing new teeth all its life.'),
    (174, 11, 'What is a dinosaur trackway?', 'A line of preserved footprints', 'A fossil bone', 'A dinosaur nest', 'A pile of eggs', 'A', 'Trackways record a dinosaur''s walk.'),
    (175, 11, 'Which famous fossil site is in Utah, USA?', 'Cleveland-Lloyd Dinosaur Quarry', 'Stonehenge', 'Grand Canyon', 'Petra', 'A', 'It holds many Allosaurus bones.'),
    (176, 11, 'What did the hollow crest of Parasaurolophus likely do?', 'Store food', 'Make sound', 'Fly', 'Dig', 'B', 'Air flowed through it to make calls.'),
    (177, 11, 'Were dinosaurs warm-blooded?', 'Yes, all of them', 'No, all were cold-blooded', 'Only flying ones', 'Scientists debate this question', 'D', 'Bones and growth hint at both ideas.'),
    (178, 11, 'Which dinosaurs often had feathers?', 'Many small theropods', 'Only giants', 'Only sea reptiles', 'No dinosaurs at all', 'A', 'Fossils show feather impressions.'),
    (179, 11, 'What protected Ankylosaurus?', 'Speed and wings', 'Bony armor and a tail club', 'A loud roar', 'Long legs only', 'B', 'It was built like a living tank.'),
    (180, 11, 'Which horned dinosaurs may have lived in herds?', 'Ceratopsians', 'Pterosaurs', 'Ichthyosaurs', 'Sauropods', 'A', 'Bonebeds suggest they traveled in groups.'),
    (181, 11, 'What do we call a baby dinosaur that just hatched?', 'A pup', 'A hatchling', 'A kitten', 'A cub', 'B', 'Hatchlings grew fast in the wild.'),
    (182, 11, 'On which supercontinent did dinosaurs first appear?', 'Laurasia', 'Gondwana', 'Pangaea', 'Atlantis', 'C', 'All the land was joined together then.'),
    (227, 12, 'What is the most accepted cause of the dinosaurs'' extinction?', 'A giant asteroid impact', 'A global drought', 'Too much snow', 'Ancient storms', 'A', 'A huge crater supports this idea.'),
    (228, 12, 'Where did the giant impact crater strike?', 'In the Pacific Ocean', 'Near Mexico''s Yucatan Peninsula', 'In the Sahara Desert', 'In Antarctica', 'B', 'The Chicxulub crater is there.'),
    (229, 12, 'What is the impact crater called?', 'Meteor Crater', 'Vredefort', 'Chesapeake Bay', 'Chicxulub', 'D', 'It is hidden below rock and water.'),
    (230, 12, 'Which element found in rock layers is a clue to the impact?', 'Gold', 'Silver', 'Iridium', 'Copper', 'C', 'Iridium is rare on Earth but common in asteroids.'),
    (231, 12, 'Which era came after the dinosaurs died out?', 'Cenozoic', 'Mesozoic', 'Paleozoic', 'Precambrian', 'A', 'Mammals rose to power in this era.'),
    (232, 12, 'Which ancient reptiles could truly fly?', 'Pterosaurs', 'Plesiosaurs', 'Ichthyosaurs', 'Mosasaurus', 'A', 'Their wings were made of skin.'),
    (233, 12, 'What is a pterodactyl''s wing made of?', 'Feathers only', 'Bone and feathers', 'A membrane of skin and muscle', 'Clouds', 'C', 'The wing stretched from body to finger.'),
    (234, 12, 'Which bird-like dinosaurs have been found fossilized in China?', 'Many small theropods', 'Only sauropods', 'Only sea reptiles', 'None at all', 'A', 'China''s rocks preserve feathered dinosaurs.');

INSERT INTO questions (id, level_id, question_text, option_a, option_b, option_c, option_d, correct_answer, hint) VALUES
    (91, 13, 'What is the name of the wall that surrounds a castle?', 'Curtain wall', 'Stone floor', 'Glass wall', 'Wooden fence', 'A', 'It wrapped around the whole castle.'),
    (92, 13, 'What was the main entrance to a castle called?', 'The stables', 'The gatehouse', 'The chapel', 'The tower', 'B', 'Guards watched the gate night and day.'),
    (93, 13, 'What heavy gate did castles lower to block entry?', 'A farm gate', 'A wagon door', 'A portcullis', 'A barn door', 'C', 'It was a heavy iron-tipped grill.'),
    (94, 13, 'Who lived inside a medieval castle?', 'Only farmers', 'Only soldiers', 'Travelling merchants', 'Nobles and their families', 'D', 'Castles were homes for the rich.'),
    (95, 13, 'What is a tall, narrow castle window called?', 'Arrow slit', 'Round window', 'Glass door', 'Sky light', 'A', 'Archers fired arrows through it.'),
    (96, 13, 'What did guards walk along at the top of castle walls?', 'The garden path', 'The battlements', 'The moat', 'The drawbridge', 'B', 'Battlements had gaps for shooting.'),
    (97, 13, 'What is the strongest stone tower in a castle called?', 'The pantry', 'The stable', 'The keep', 'The mill', 'C', 'The keep was the last safe place.'),
    (98, 13, 'What body of water often surrounded a castle for defense?', 'A lake', 'An ocean', 'A river', 'A moat', 'D', 'A moat could be filled with water.'),
    (99, 13, 'What bridge could be raised to block a castle gate?', 'Drawbridge', 'Stone bridge', 'Rope bridge', 'Log bridge', 'A', 'It was lowered only for friends.'),
    (100, 13, 'Which room stored a castle''s food?', 'The armory', 'The pantry', 'The dungeon', 'The tower', 'B', 'Pantries kept grain and dried meats.'),
    (101, 13, 'Where did knights keep their horses?', 'In the great hall', 'In the kitchen', 'In the stables', 'In the moat', 'C', 'Stables held horses and supplies.'),
    (102, 13, 'What did knights wear to protect their heads?', 'Tall hats', 'Helmets', 'Crowns', 'Hoods', 'B', 'Helmets were made of metal.'),
    (103, 13, 'Which weapon did knights use while riding horses?', 'A rake', 'A fork', 'A fishing rod', 'A lance', 'D', 'A lance was a long spear.'),
    (104, 13, 'What did a king wear on his head?', 'A crown', 'A helmet only', 'A scarf', 'A nightcap', 'A', 'Crowns showed royal power.'),
    (105, 13, 'Who worked in the fields for the lord?', 'The knights', 'The king', 'The merchants', 'The peasants', 'D', 'Most people were peasants.'),
    (106, 13, 'Where did medieval towns hold their markets?', 'In the forest', 'On the roof', 'In the market square', 'In the moat', 'C', 'Traders set up stalls there.'),
    (107, 13, 'Which building stood at the center of village life?', 'The church', 'The stable', 'The dungeon', 'The tower', 'A', 'People gathered there on Sundays.'),
    (108, 13, 'What did knights hold to block swords and arrows?', 'A sack', 'A shield', 'A ladder', 'A basket', 'B', 'Shields were often painted with signs.'),
    (109, 13, 'What was the big meeting and feasting room in a castle?', 'The great hall', 'The stable', 'The cellar', 'The pantry', 'A', 'Feasts and courts happened there.'),
    (110, 13, 'Who advised the king on important matters?', 'The peasants', 'The horses', 'The minstrels', 'The nobles', 'D', 'Nobles were powerful lords.'),
    (111, 13, 'What did a castle bell often warn people of?', 'Danger', 'Dinner only', 'Bedtime only', 'Market prices', 'A', 'Bells signaled alarms and times.'),
    (112, 13, 'Which metals were used to make a knight''s armor?', 'Iron and steel', 'Gold and silver', 'Copper and tin', 'Lead only', 'A', 'Iron and steel were strong and tough.'),
    (113, 13, 'What did loyal knights receive from their king?', 'A patch of sea', 'Land', 'A flock of birds', 'A sack of apples', 'B', 'Land brought wealth and power.'),
    (114, 13, 'What is a medieval singer of tales called?', 'A blacksmith', 'A tailor', 'A minstrel', 'A baker', 'C', 'Minstrels traveled and sang for lords.'),
    (115, 13, 'What was the open courtyard inside castle walls called?', 'The bailey', 'The dungeon', 'The stable', 'The tower', 'A', 'People stored carts and animals there.'),
    (116, 13, 'What did peasants grow in their fields?', 'Wheat and barley', 'Gold and silver', 'Stones', 'Seaweed', 'A', 'Crops fed the whole manor.'),
    (117, 13, 'Which craft used hot ovens to make bread?', 'Baking', 'Sewing', 'Carpentry', 'Smithing', 'A', 'Bakers worked in the town.'),
    (118, 13, 'What did knights do at tournaments?', 'Sell cloth', 'Plant crops', 'Compete in jousts', 'Fish in the moat', 'C', 'Knights fought with lances on horseback.'),
    (183, 14, 'What was the code of honor for knights called?', 'Chivalry', 'Slavery', 'Bartering', 'Taxation', 'A', 'It meant courage, loyalty, and courtesy.'),
    (184, 14, 'What did a knight promise to give his lord?', 'All his gold', 'Loyalty', 'His castle', 'His kingdom', 'B', 'Loyalty held the whole system together.'),
    (185, 14, 'Which weapon was a heavy ball swung on a chain?', 'A bow', 'A spear', 'A club', 'A flail', 'D', 'A flail could smash through armor.'),
    (186, 14, 'What did a squire do for a knight?', 'Serve and train', 'Rule the land', 'Collect taxes', 'Crown the king', 'A', 'A squire learned to fight and care for armor.'),
    (187, 14, 'Which knight served the legendary King Arthur?', 'Robin Hood', 'Lancelot', 'Hercules', 'Spartacus', 'B', 'Lancelot was a famous knight of the Round Table.'),
    (188, 14, 'What was a knight''s war horse called?', 'A pony', 'A mule', 'A destrier', 'A donkey', 'C', 'Destriers were strong and fast.'),
    (189, 14, 'What did a gauntlet protect?', 'The hand', 'The head', 'The chest', 'The foot', 'A', 'Gauntlets were the gloves of armor.'),
    (190, 14, 'What is the hilt of a sword?', 'The sharp edge', 'The handle', 'The tip', 'The point', 'B', 'The knight gripped the hilt.'),
    (191, 14, 'Who trained a young page for knighthood?', 'His cook', 'His tailor', 'His lord', 'His baker', 'C', 'Pages learned manners and skills at court.'),
    (192, 14, 'Which armor was made from linked metal rings?', 'Chain mail', 'Leather boots', 'Wooden plates', 'Cloth layers', 'A', 'Rings let the wearer move freely.'),
    (193, 14, 'What did a knight carry to show his family identity?', 'A shopping list', 'A map', 'A coin', 'A coat of arms', 'D', 'Coats of arms decorated shields and banners.'),
    (194, 14, 'Which weapon shot arrows?', 'The longbow', 'The flail', 'The mace', 'The lance', 'A', 'Longbows could shoot very far.'),
    (195, 14, 'What protected a knight''s chest?', 'The gauntlet', 'The helmet', 'The breastplate', 'The greave', 'C', 'The breastplate covered the torso.'),
    (196, 14, 'Who were serfs in medieval society?', 'Peasants bound to the land', 'Rich merchants', 'Royal advisors', 'Visiting knights', 'A', 'Serfs worked the lord''s land.'),
    (197, 14, 'What was the medieval social system of land for loyalty called?', 'Democracy', 'Republic', 'Feudalism', 'Empire', 'C', 'Lords gave land for service.'),
    (198, 14, 'Which legendary outlaw stole from the rich?', 'King Arthur', 'Lancelot', 'Gawain', 'Robin Hood', 'D', 'He shared with the poor in Sherwood Forest.'),
    (199, 14, 'What marked the end of a young man''s training?', 'Becoming a knight', 'Buying a horse', 'Building a castle', 'Sailing the sea', 'A', 'A ceremony made him a knight.'),
    (200, 14, 'What was a castle dungeon used for?', 'Storing grain', 'Holding prisoners', 'Keeping horses', 'Hosting feasts', 'B', 'Dungeons were dark prison rooms.'),
    (235, 15, 'What shaped the daily life of medieval peasants most?', 'The seasons and farming', 'The tides', 'The stars only', 'The moon only', 'A', 'Crops were planted and harvested by season.'),
    (236, 15, 'Which terrible plague swept across medieval Europe?', 'The Great Flu', 'The Black Death', 'Chickenpox', 'Measles', 'B', 'It killed millions in the 1300s.'),
    (237, 15, 'What did lords give to peasants in exchange for their work?', 'Gold coins', 'Horses', 'Land and protection', 'Royal titles', 'C', 'Peasants farmed in return for safety.'),
    (238, 15, 'Who were the fierce raiders from Scandinavia?', 'The Romans', 'The Greeks', 'The Mongols', 'The Vikings', 'D', 'Vikings sailed in long ships.'),
    (239, 15, 'What did guilds do in medieval towns?', 'Protect crafts and trade', 'Wage wars only', 'Rule the church', 'Collect the harvest', 'A', 'Guilds set standards for craft work.'),
    (240, 15, 'Which metals were used to make medieval coins?', 'Gold and silver', 'Iron and steel', 'Lead and tin', 'Copper only', 'A', 'Precious metals backed the money.'),
    (241, 15, 'Which battle ended the Wars of the Roses?', 'The Battle of Hastings', 'The Battle of Agincourt', 'The Battle of Bosworth Field', 'The Battle of Bannockburn', 'C', 'It put Henry Tudor on the throne.'),
    (242, 15, 'What else was the great hall used for besides feasts?', 'Court hearings', 'Sewing clothes', 'Fishing', 'Storing hay', 'A', 'Lords held court and judged disputes there.');

INSERT INTO questions (id, level_id, question_text, option_a, option_b, option_c, option_d, correct_answer, hint) VALUES
    (119, 16, 'What is the bottom layer of the rainforest called?', 'Forest floor', 'Canopy', 'Emergent layer', 'Sky layer', 'A', 'It is dark and covered with leaves.'),
    (120, 16, 'Which animals are found in the rainforest canopy?', 'Whales and seals', 'Monkeys and birds', 'Camels and goats', 'Polar bears', 'B', 'Canopy dwellers are expert climbers.'),
    (121, 16, 'What do rainforest plants need most?', 'Snow and ice', 'Sand only', 'Sunlight and rain', 'Salt only', 'C', 'The rainforest is warm and wet.'),
    (122, 16, 'What color are most tree frogs?', 'Gray', 'White', 'Black', 'Bright green', 'D', 'Green helps them hide in leaves.'),
    (123, 16, 'Which big cat lives in the rainforest?', 'Jaguar', 'Lion', 'Cheetah', 'Snow leopard', 'A', 'Jaguars are strong, spotted hunters.'),
    (124, 16, 'What do rainforest trees give us?', 'Oxygen and shelter', 'Snow and ice', 'Sand and salt', 'Clouds only', 'A', 'Trees make oxygen for us to breathe.'),
    (125, 16, 'Which bird can learn to repeat words?', 'Penguin', 'Ostrich', 'Parrot', 'Owl', 'C', 'Parrots are clever talkers.'),
    (126, 16, 'What is the very top layer of tall trees called?', 'Forest floor', 'Understory', 'Root layer', 'Emergent layer', 'D', 'The tallest trees rise above the rest.'),
    (127, 16, 'Which ant builds large nests high in trees?', 'Weaver ant', 'Fire ant', 'Carpenter ant', 'Sugar ant', 'A', 'They sew leaves together with silk.'),
    (128, 16, 'What do sloths do for most of the day?', 'Fly', 'Sing', 'Swim', 'Sleep', 'D', 'Sloths can sleep up to 20 hours.'),
    (129, 16, 'Which fruit grows on rainforest trees?', 'Icebergs', 'Stones', 'Banana', 'Cobwebs', 'C', 'Bananas grow in the tropics.'),
    (130, 16, 'What is the middle layer of the rainforest called?', 'Rooftop', 'Sea floor', 'Sky', 'Understory', 'D', 'It sits below the canopy.'),
    (131, 16, 'Which reptile hangs from trees?', 'Snake', 'Whale', 'Polar bear', 'Penguin', 'A', 'Tree snakes glide through the branches.'),
    (132, 16, 'Why is the rainforest so green?', 'Because there are so many plants', 'Because it snows', 'Because of the ocean', 'Because of the sand', 'A', 'Thousands of plant species grow there.'),
    (133, 16, 'Which animal has a mask of dark fur around its eyes?', 'Toucan', 'Raccoon', 'Sloth', 'Macaw', 'B', 'The dark mask is its most famous feature.'),
    (134, 16, 'Which bird has a huge colorful beak?', 'Owl', 'Penguin', 'Toucan', 'Ostrich', 'C', 'Its beak is big but very light.'),
    (135, 16, 'Which is the world''s smallest monkey?', 'Pygmy marmoset', 'Gorilla', 'Baboon', 'Orangutan', 'A', 'It can sit in the palm of your hand.'),
    (136, 16, 'Why is the rainforest so wet?', 'Because it rains heavily', 'Because of melting ice', 'Because of the sea', 'Because of clouds only', 'A', 'It can rain for hours every day.'),
    (137, 16, 'Which butterfly has brilliant blue wings?', 'Monarch', 'Painted lady', 'Blue morpho', 'Swallowtail', 'C', 'Its blue shimmer is famous.'),
    (138, 16, 'What do many rainforest frogs lay in the water?', 'Feathers', 'Seeds', 'Stones', 'Eggs', 'D', 'Frog eggs hatch into tadpoles.'),
    (139, 16, 'Which plant climbs up tree trunks?', 'Vine', 'Cactus', 'Wheat', 'Tulip', 'A', 'Vines reach the light high above.'),
    (140, 16, 'Which big colorful bird lives in the Amazon?', 'Emperor penguin', 'Macaw', 'Pigeon', 'Duck', 'B', 'Macaws have bright red and blue feathers.'),
    (141, 16, 'What do toucans use their big beaks to reach?', 'Insects in the mud', 'Fruit on thin branches', 'Fish in the river', 'Honey in the ground', 'B', 'The beak reaches food at the branch tips.'),
    (142, 16, 'Which monkey swings by its tail?', 'Baboon', 'Spider monkey', 'Chimpanzee', 'Rhesus monkey', 'B', 'Its tail works like a fifth hand.'),
    (143, 16, 'What color is the leaf of most trees?', 'Blue', 'Purple', 'Pink', 'Green', 'D', 'Chlorophyll makes leaves green.'),
    (144, 16, 'Which small wild cat leaps between tree branches?', 'Lion', 'Tiger', 'Margay', 'Puma', 'C', 'Margays can jump from branch to branch.'),
    (145, 16, 'Which creature spins webs between trees?', 'Whale', 'Penguin', 'Spider', 'Seal', 'C', 'Spiders spin silky webs to catch prey.'),
    (146, 16, 'Which bird drums on tree trunks for insects?', 'Flamingo', 'Swan', 'Goose', 'Woodpecker', 'D', 'Its beak taps bark to find bugs.'),
    (201, 17, 'Which ape builds a new nest high in trees each night?', 'Orangutan', 'Lion', 'Walrus', 'Giraffe', 'A', 'Orangutans sleep in leafy nests.'),
    (202, 17, 'Which huge snake squeezes its prey?', 'Grass snake', 'Anaconda', 'Garter snake', 'Corn snake', 'B', 'Anacondas coil around their prey.'),
    (203, 17, 'What do leaf-cutter ants grow from the leaves they collect?', 'Flowers', 'Seeds', 'Fungus', 'Fruit', 'C', 'They farm fungus for food.'),
    (204, 17, 'Which frog is brightly colored and poisonous?', 'Poison dart frog', 'Bullfrog', 'Tree frog', 'Glass frog', 'A', 'Its bright colors warn hunters.'),
    (205, 17, 'Which flying mammal lives in rainforest caves?', 'Owl', 'Bat', 'Eagle', 'Hawk', 'B', 'Bats fly out to hunt at night.'),
    (206, 17, 'Which great ape lives in African forests?', 'Panda', 'Koala', 'Gorilla', 'Chimpanzee', 'C', 'Gorillas are strong plant-eaters.'),
    (207, 17, 'What do canopy plants get from growing high up?', 'More sunlight', 'More water', 'More soil', 'More wind', 'A', 'Light is easiest to reach up high.'),
    (208, 17, 'Which eagle hunts monkeys from the canopy?', 'Harpy eagle', 'Bald eagle', 'Golden eagle', 'Osprey', 'A', 'The harpy eagle is huge and powerful.'),
    (209, 17, 'Which insect looks exactly like a leaf?', 'Ladybug', 'Walking leaf insect', 'Dragonfly', 'Cricket', 'B', 'It hides from hunters by disguise.'),
    (210, 17, 'Which rodent is the world''s largest?', 'Mouse', 'Rat', 'Squirrel', 'Capybara', 'D', 'Capybaras can weigh over 50 kilograms.'),
    (211, 17, 'Which flowers grow on rainforest trees?', 'Cactus', 'Orchids', 'Tulips', 'Daisies', 'B', 'Many orchids bloom high in the canopy.'),
    (212, 17, 'Which crocodilian lurks in Amazon rivers?', 'Alligator', 'Crocodile', 'Gharial', 'Caiman', 'D', 'Caimans are close cousins of alligators.'),
    (213, 17, 'Which snake is one of the deadliest pit vipers in the rainforest?', 'Fer-de-lance', 'Garter snake', 'Corn snake', 'Milk snake', 'A', 'Its venom is very powerful.'),
    (214, 17, 'Which fish builds nests and guards its eggs?', 'Clownfish only', 'Some cichlids', 'Tuna', 'Salmon', 'B', 'Parent fish watch over the young.'),
    (215, 17, 'What do sloths mostly eat?', 'Fish', 'Nuts only', 'Leaves', 'Fruit only', 'C', 'Leaves are slow to digest, so sloths rest a lot.'),
    (216, 17, 'Which big bird has a helmet-like casque on its head?', 'Hornbill', 'Penguin', 'Ostrich', 'Kiwi', 'A', 'The casque sits on top of its beak.'),
    (217, 17, 'Which insects build giant mound colonies?', 'Bees', 'Wasps', 'Ants', 'Termites', 'D', 'Termite mounds can be taller than a person.'),
    (218, 17, 'Which colorful lizard climbs on trees?', 'Anole', 'Tortoise', 'Iguana', 'Gecko', 'A', 'Anoles can change their color.'),
    (243, 18, 'What is the first priority if you get lost in a jungle?', 'Finding safe water', 'Collecting gold', 'Counting stars', 'Building a raft', 'A', 'You can survive days without food but not water.'),
    (244, 18, 'What makes a good jungle shelter?', 'Leaves and branches', 'Snow blocks', 'Glass panes', 'Metal sheets', 'A', 'Natural materials keep off the rain.'),
    (245, 18, 'How can you signal for help from the air?', 'Hide under trees', 'Build a campfire on the beach only', 'Lay out bright ground signals', 'Climb a cloud', 'C', 'Big letters and bright colors catch the eye.'),
    (246, 18, 'Which sound carries farthest in a jungle?', 'A whisper', 'A whistle', 'A hand clap', 'A cough', 'B', 'Whistles are loud and sharp.'),
    (247, 18, 'How can you use a watch to find north?', 'Point the hour hand at the sun', 'Spin it in the air', 'Look at the glass', 'Hold it to your ear', 'A', 'Halfway between the hour hand and 12 is south.'),
    (248, 18, 'Which is a sign that water is safe to drink?', 'It smells like mud', 'It flows over rocks', 'It is full of foam', 'It is bright green', 'B', 'Moving water is usually cleaner.'),
    (249, 18, 'When is the best time to travel in the hot jungle?', 'Midday', 'Early morning', 'Afternoon', 'Late night', 'B', 'The cool hours save your energy.'),
    (250, 18, 'What should you do if you meet a wild animal?', 'Chase it', 'Throw rocks', 'Back away slowly', 'Run away fast', 'C', 'Moving slowly is less threatening.');

INSERT INTO player_progress (id, user_id, level_id, correct_answers, total_questions, points_earned, completed, completed_at) VALUES
    (1, 1, 1, 2, 2, 20, 1, '2026-08-01 10:30:00'),
    (2, 1, 4, 1, 2, 10, 1, '2026-08-02 15:45:00'),
    (3, 2, 1, 1, 2, 10, 0, NULL);

INSERT INTO wow_levels (id, world_id, name, difficulty, theme, words, point_reward) VALUES
    (1, 1, 'Pyramid Puzzle', 'easy', 'Ancient Egypt', '["PYRAMID","SPHINX","MUMMY","TOMB","NILE"]', 80),
    (2, 1, 'Pharaoh Challenge', 'medium', 'Ancient Egypt', '["PYRAMID","SPHINX","MUMMY","TOMB","NILE","PHARAOH","SCARAB"]', 150),
    (3, 1, 'Ancient Secrets', 'hard', 'Ancient Egypt', '["PYRAMID","SPHINX","MUMMY","TOMB","NILE","PHARAOH","SCARAB","OBELISK"]', 250),
    (4, 2, 'Star Gazer', 'easy', 'Outer Space', '["PLANET","COMET","ORBIT","NOVA"]', 80),
    (5, 2, 'Cosmic Explorer', 'medium', 'Outer Space', '["PLANET","COMET","ORBIT","NOVA","GALAXY","NEBULA"]', 150),
    (6, 2, 'Deep Space', 'hard', 'Outer Space', '["PLANET","COMET","ORBIT","NOVA","GALAXY","NEBULA","QUASAR","PULSAR"]', 250),
    (7, 3, 'Coral Explorer', 'easy', 'The Deep Ocean', '["CORAL","WHALE","REEF","TIDE"]', 80),
    (8, 3, 'Ocean Depths', 'medium', 'The Deep Ocean', '["CORAL","WHALE","REEF","TIDE","SHELL","KELP"]', 150),
    (9, 3, 'Abyssal Quest', 'hard', 'The Deep Ocean', '["CORAL","WHALE","REEF","TIDE","SHELL","KELP","ABYSS","MARINE"]', 250),
    (10, 4, 'Rex Runner', 'easy', 'Dinosaur World', '["REX","CLAW","BONE","JAW"]', 80),
    (11, 4, 'Fossil Hunt', 'medium', 'Dinosaur World', '["REX","CLAW","BONE","JAW","FOSSIL","SCALE"]', 150),
    (12, 4, 'Jurassic Trail', 'hard', 'Dinosaur World', '["REX","CLAW","BONE","JAW","FOSSIL","SCALE","TALON","ROAR"]', 250),
    (13, 5, 'Castle Builder', 'easy', 'Medieval Kingdoms', '["CASTLE","SWORD","SHIELD","CROWN"]', 80),
    (14, 5, 'Knight Quest', 'medium', 'Medieval Kingdoms', '["CASTLE","SWORD","SHIELD","CROWN","QUEST","DRAGON"]', 150),
    (15, 5, 'King''s Challenge', 'hard', 'Medieval Kingdoms', '["CASTLE","SWORD","SHIELD","CROWN","QUEST","DRAGON","KNIGHT","ROYAL"]', 250),
    (16, 6, 'Canopy Walk', 'easy', 'Rainforest Adventure', '["VINE","LEAF","FROG","NEST"]', 80),
    (17, 6, 'Jungle Trek', 'medium', 'Rainforest Adventure', '["VINE","LEAF","FROG","NEST","CANOPY","ORCHID"]', 150),
    (18, 6, 'Wild Expedition', 'hard', 'Rainforest Adventure', '["VINE","LEAF","FROG","NEST","CANOPY","ORCHID","MACAW","TROPIC"]', 250);

-- ═══════════════════════════════════════════════════════════════════════
-- NEW TABLES: Game Sessions, Achievements, Daily Challenges, Streaks
-- ═══════════════════════════════════════════════════════════════════════

DROP TABLE IF EXISTS daily_completions CASCADE;
DROP TABLE IF EXISTS user_streaks CASCADE;
DROP TABLE IF EXISTS daily_challenges CASCADE;
DROP TABLE IF EXISTS user_achievements CASCADE;
DROP TABLE IF EXISTS achievements CASCADE;
DROP TABLE IF EXISTS game_sessions CASCADE;

CREATE TABLE IF NOT EXISTS game_sessions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    game_type VARCHAR(30) NOT NULL,
    points_earned INT NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    duration_seconds INT DEFAULT 0,
    star_rating INT NOT NULL DEFAULT 0,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS achievements (
    id SERIAL PRIMARY KEY,
    key VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(10) NOT NULL,
    condition_type VARCHAR(50) NOT NULL,
    condition_value INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS user_achievements (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    achievement_id INT NOT NULL,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, achievement_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS daily_challenges (
    id SERIAL PRIMARY KEY,
    challenge_date DATE NOT NULL UNIQUE,
    game_type VARCHAR(30) NOT NULL,
    config_json TEXT NOT NULL,
    bonus_multiplier INT NOT NULL DEFAULT 2
);

CREATE TABLE IF NOT EXISTS user_streaks (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    current_streak INT NOT NULL DEFAULT 0,
    longest_streak INT NOT NULL DEFAULT 0,
    last_play_date DATE,
    streak_freezes INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS daily_completions (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    challenge_date DATE NOT NULL,
    points_earned INT NOT NULL DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, challenge_date),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ═══════════════════════════════════════════════════════════════════════
-- SEED: 20 Achievements
-- ═══════════════════════════════════════════════════════════════════════

INSERT INTO achievements (key, name, description, icon, condition_type, condition_value) VALUES
    ('first_steps',    'First Steps',    'Complete your first game',                          '🌟', 'total_completed', 1),
    ('on_fire',        'On Fire',        'Maintain a 7-day play streak',                      '🔥', 'streak',          7),
    ('champion',       'Champion',       'Reach 1000 total points',                           '🏆', 'total_points',    1000),
    ('quiz_master',    'Quiz Master',    'Complete 100 quiz games',                            '🧠', 'quiz_correct',    100),
    ('word_hunter',    'Word Hunter',    'Complete 50 word search puzzles',                    '🔍', 'wordsearch_words', 50),
    ('word_wizard',    'Word Wizard',    'Complete 10 Words of Wonders puzzles',               '💎', 'wow_completed',   10),
    ('alchemist',      'Alchemist',      'Complete 10 Water Sort levels',                      '🧪', 'cups_completed',  10),
    ('speed_demon',    'Speed Demon',    'Complete 20 games total',                            '⚡', 'total_completed', 20),
    ('dedicated',      'Dedicated',      'Maintain a 30-day play streak',                     '📅', 'streak',          30),
    ('world_traveler', 'World Traveler', 'Complete 50 games total — you have traveled far!',  '🌍', 'total_completed', 50),
    ('perfect_score',  'Perfect Score',  'Earn 10 three-star ratings',                         '🎯', 'total_stars',     30),
    ('century',        'Century',        'Maintain a 100-day play streak',                     '💯', 'longest_streak',  100),
    ('deep_diver',     'Deep Diver',     'Complete 100 games total',                           '🐋', 'total_completed', 100),
    ('top_player',     'Top Player',     'Reach 5000 total points',                            '🏅', 'total_points',    5000),
    ('all_rounder',    'All-Rounder',    'Play all 4 game modes at least once',                '🎮', 'all_modes',       1),
    ('star_collector', 'Star Collector', 'Earn 60 total stars across all games',               '⭐', 'total_stars',     60),
    ('puzzle_king',    'Puzzle King',    'Complete 200 total puzzles',                          '🧩', 'total_completed', 200),
    ('getting_started','Getting Started','Play 10 games total',                                '🕐', 'total_played',    10),
    ('point_master',   'Point Master',   'Reach 10000 total points',                           '🤝', 'total_points',    10000),
    ('legend',         'Legend',         'Earn 19 other achievements — you are a true legend!', '👑', 'achievement_count', 19);