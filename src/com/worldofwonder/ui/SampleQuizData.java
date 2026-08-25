package com.worldofwonder.ui;

import com.worldofwonder.model.Level;
import com.worldofwonder.model.Question;
import com.worldofwonder.model.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SampleQuizData {

    private final List<World> worlds;
    private final Map<Integer, List<Level>> levelsByWorld;
    private final Map<Integer, List<Question>> questionsByLevel;

    SampleQuizData() {
        this.worlds = new ArrayList<>();
        this.levelsByWorld = new HashMap<>();
        this.questionsByLevel = new HashMap<>();
        build();
    }

    List<World> getWorlds() {
        return Collections.unmodifiableList(worlds);
    }

    List<Level> getLevels(int worldId) {
        List<Level> levels = levelsByWorld.get(worldId);
        return levels == null ? Collections.emptyList() : Collections.unmodifiableList(levels);
    }

    List<Question> getQuestions(int levelId) {
        List<Question> questions = questionsByLevel.get(levelId);
        return questions == null ? Collections.emptyList() : Collections.unmodifiableList(questions);
    }

    private void addWorld(int id, String name, String description) {
        worlds.add(new World(id, name, description));
        levelsByWorld.put(id, new ArrayList<>());
    }

    private void addLevel(int id, int worldId, String name, String difficulty, int reward) {
        levelsByWorld.get(worldId).add(new Level(id, worldId, name, difficulty, reward));
        questionsByLevel.put(id, new ArrayList<>());
    }

    private void addQuestion(int levelId, String text, String a, String b, String c, String d,
                             String correct, String hint) {
        questionsByLevel.get(levelId)
                .add(new Question(0, levelId, text, a, b, c, d, correct, hint));
    }

    private void build() {
        addWorld(1, "Ancient Egypt", "Pyramids, pharaohs and the mighty Nile.");
        addWorld(2, "Outer Space", "Explore planets, stars and the galaxy.");
        addWorld(3, "The Deep Ocean", "Dive into reefs and the mysterious deep sea.");

        addLevel(1, 1, "The Nile", "easy", 100);
        addQuestion(1, "Which river flows through Egypt?",
                "Amazon", "Nile", "Ganges", "Thames", "B", "It is the longest river in the world.");
        addQuestion(1, "What is the Egyptian sun god called?",
                "Ra", "Zeus", "Odin", "Thor", "A", "Its name starts with the letter R.");
        addQuestion(1, "The ancient Egyptians wrote using...",
                "Runes", "Cuneiform", "Hieroglyphs", "Latin", "C", "They are picture symbols carved in stone.");

        addLevel(2, 1, "Pyramids", "medium", 150);
        addQuestion(2, "The Great Pyramid of Giza was a tomb for which pharaoh?",
                "Tutankhamun", "Ramses II", "Khufu", "Cleopatra", "C", "He built the Great Pyramid of Giza.");
        addQuestion(2, "The Great Sphinx has the body of a lion and the head of a...",
                "Cat", "Human", "Crocodile", "Falcon", "B", "It guards the pyramids of Giza.");
        addQuestion(2, "Cleopatra was the last ruler of which dynasty?",
                "Ptolemaic", "Roman", "Macedonian", "Ottoman", "A", "Founded by a general of Alexander the Great.");

        addLevel(3, 2, "The Solar System", "easy", 100);
        addQuestion(3, "Which planet is closest to the Sun?",
                "Venus", "Mercury", "Earth", "Mars", "B", "It is the smallest and fastest planet.");
        addQuestion(3, "Which planet is known as the Red Planet?",
                "Jupiter", "Saturn", "Mars", "Neptune", "C", "Named after the Roman god of war.");
        addQuestion(3, "The Sun is made mostly of...",
                "Iron", "Hydrogen", "Oxygen", "Gold", "B", "It fuses this gas to create energy.");

        addLevel(4, 2, "Galaxies", "hard", 200);
        addQuestion(4, "The closest star to Earth is...",
                "Sirius", "Betelgeuse", "The Sun", "Proxima Centauri", "C", "It rises every morning.");
        addQuestion(4, "How many planets are there in our solar system?",
                "Seven", "Eight", "Nine", "Ten", "B", "Pluto used to be one of them.");
        addQuestion(4, "Saturn is famous for its...",
                "Moons", "Rings", "Storms", "Volcanoes", "B", "They are made of ice and rock.");

        addLevel(5, 3, "Coral Reefs", "easy", 100);
        addQuestion(5, "Which of these is a living creature?",
                "Coral", "Rock", "Shell", "Sand", "A", "It looks like a plant but is an animal.");
        addQuestion(5, "Clownfish live safely inside which creature?",
                "Sea urchin", "Sea anemone", "Starfish", "Jellyfish", "B", "Its tentacles protect the fish.");
        addQuestion(5, "What colour is most coral reef water?",
                "Red", "Green", "Blue", "Purple", "C", "The sky reflects into it.");

        addLevel(6, 3, "Deep Sea", "medium", 150);
        addQuestion(6, "Which animal is the largest to ever live on Earth?",
                "Blue whale", "Elephant", "Megalodon", "Giant squid", "A", "It can weigh over 150 tonnes.");
        addQuestion(6, "A jellyfish is made mostly of...",
                "Water", "Bone", "Muscle", "Sand", "A", "It is about 95% of this.");
        addQuestion(6, "Which creature can change colour to hide from predators?",
                "Octopus", "Dolphin", "Whale", "Sea turtle", "A", "It has eight arms and three hearts.");
    }
}
