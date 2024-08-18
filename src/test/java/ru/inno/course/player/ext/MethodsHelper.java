package ru.inno.course.player.ext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class MethodsHelper {
    private PlayerService service;

    public MethodsHelper(PlayerService service) {
        this.service = service;
    }

    public void createListOfPlayers() {
        int player1 = service.createPlayer("Player1");
        service.addPoints(player1, 1);
        int player2 = service.createPlayer("Player2");
        service.addPoints(player2, 2);
        int player3 = service.createPlayer("Player3");
        service.addPoints(player3, 3);
    }

    public Collection<Player> readFile() throws IOException {
        Path filePath = Path.of("./fileForTest.json");
        ObjectMapper mapper = new ObjectMapper();
        List<Player> getPlayers = mapper.readValue(filePath.toFile(), new TypeReference<>() {
        });
        return getPlayers;
    }

    public void addToFilePlayersWithFile() throws IOException {
        Collection<Player> getPlayers = readFile();
        List<Player> listOfPlaers = getPlayers.stream().toList();
        for (Player players : listOfPlaers) {
            service.createPlayer(players.getNick());
        }
    }

    public void getPlayersWithFile() throws IOException {
        Collection<Player> getPlayers = readFile();
        List<Player> listOfPlaers = getPlayers.stream().toList();
        for (Player players : listOfPlaers) {
            service.getPlayerById(players.getId());
        }
    }

    public void doNewFile() throws IOException {
        Path filePath = Path.of("./data.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(filePath.toFile(), "[{\n" + " \"name\": \"Molecule Man\"\n" + "}]");
    }
}
