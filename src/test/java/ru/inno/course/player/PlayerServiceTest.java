package ru.inno.course.player;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import ru.inno.course.player.ext.MethodsHelper;
import ru.inno.course.player.ext.PlayersAndPointsProvider;
import ru.inno.course.player.ext.PointsProvider;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerServiceTest {
    private PlayerService service;
    private MethodsHelper helper;
    private static final String NICKNAME = "Renat";


    // hooks
    @BeforeEach
    public void setUp() {
        service = new PlayerServiceImpl();
        helper = new MethodsHelper(service);
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
    }


    @Test
    @DisplayName("Создаем игрока и проверяем его значения по дефолту")
    public void iCanAddNewPlayer() {
        Collection<Player> listBefore = service.getPlayers();
        assertThat(listBefore).hasSize(0);

        int renatId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(renatId);

        assertThat(playerById.getId()).isEqualTo(renatId);
        assertThat(playerById.getPoints()).isEqualTo(0);
        assertThat(playerById.getNick()).isEqualTo(NICKNAME);
        assertThat(playerById.isOnline()).isTrue();

    }


    @Test
    @DisplayName("Нельзя создать дубликат игрока")
    public void iCannotCreateADuplicate() {
        service.createPlayer(NICKNAME);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.createPlayer(NICKNAME));
    }


    @Test
    @DisplayName("Нельзя получить несуществующего пользователя")
    public void iCannotGetEmptyUser() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> service.getPlayerById(9999));
    }


    @ParameterizedTest
    @ValueSource(ints = {10, 100, -50, 0, 100, -5000000})
    @DisplayName("Добавление очков игроку")
    public void iCanAddPoints(int points) {
        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, points);
        Player playerById = service.getPlayerById(playerId);
        assertThat(playerById.getPoints()).isEqualTo(points);
    }


    @ParameterizedTest
    @ArgumentsSource(PointsProvider.class)
    @DisplayName("Добавление очков игроку")
    public void iCanAddPoints2(int pointsToAdd, int pointsToBe) {
        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, pointsToAdd);
        Player playerById = service.getPlayerById(playerId);
        assertThat(playerById.getPoints()).isEqualTo(pointsToBe);
    }


    @ParameterizedTest
    @ArgumentsSource(PlayersAndPointsProvider.class)
    @DisplayName("Добавление очков игроку c не нулевым балансом")
    public void iCanAddPoints3(Player player, int pointsToAdd, int pointsToBe) {
        int id = service.createPlayer(player.getNick());
        service.addPoints(id, player.getPoints());

        service.addPoints(id, pointsToAdd);
        Player playerById = service.getPlayerById(id);
        assertThat(playerById.getPoints()).isEqualTo(pointsToBe);
    }


    @Test
    @DisplayName("Удаляем игрока и проверяем, что игрок удалился")
    public void deletePlayer() {
        Collection<Player> listBefore = service.getPlayers();
        assertThat(listBefore.size()).isEqualTo(0);

        int renatid = service.createPlayer(NICKNAME);
        Collection<Player> listAfter = service.getPlayers();
        assertThat(listAfter.size()).isEqualTo(1);
        service.deletePlayer(renatid);
        assertThat(listBefore.size()).isEqualTo(0);
    }


    @Test
    @DisplayName("Создаем игрока, когда уже есть файл")
    public void iCanAddNewPlayerWithFile() {

        service.createPlayer(NICKNAME);
        Collection<Player> listBefore = service.getPlayers();
        assertTrue(listBefore.size() != 0);
        int newPlayer = service.createPlayer("TestTestovich");
        Player playerById = service.getPlayerById(newPlayer);

        assertThat(playerById.getId()).isEqualTo(newPlayer);
        assertThat(playerById.getPoints()).isEqualTo(0);
        assertThat(playerById.getNick()).isEqualTo("TestTestovich");
        assertThat(playerById).matches(p -> p.isOnline());
    }


    @Test
    @DisplayName("Получение игрока по id")
    public void getPlayerById() {
        int renatId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(renatId);

        assertThat(playerById.getId()).isEqualTo(renatId);
    }


    @Test
    @DisplayName("Проверяем уникальность ID")
    public void checkUniquenessId() {
        service.createPlayer("Player1");
        int idPlayer2 = service.createPlayer("Player2");
        service.createPlayer("Player3");
        service.deletePlayer(idPlayer2);
        int idPlayer4 = service.createPlayer("Player4");
        assertThat(idPlayer4).isEqualTo(4);
    }


    @Test
    @DisplayName("Проверяем создание игрока с 15 символами")
    public void iCanAddNewPlayerWith15Symbols() {
        Collection<Player> listBefore = service.getPlayers();
        assertThat(listBefore.size()).isEqualTo(0);

        String name = "Player123456789";
        int renatId = service.createPlayer(name);
        Player playerById = service.getPlayerById(renatId);

        assertThat(playerById.getNick()).isEqualTo(name);
    }


    @Test
    @DisplayName("Нельзя удалить игрока, которого нет")
    public void deliteNonExistenPlayer() {
        service.createPlayer("Player1");
        service.createPlayer("Player2");

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> service.deletePlayer(9999));
    }


    @Test
    @DisplayName("Нельзя добавить игрока без имени")
    public void dontAddPlayerWithoutName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> service.createPlayer(""));
    }


    @Test
    @DisplayName("Нельзя добавить очки игроку, которого нет")
    public void dontAddPointsNonExistentPlayer() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> service.addPoints(9999, 10));
    }


    @Test
    @DisplayName("Проверяем создание игрока с 16 символами")
    public void iCanAddNewPlayerWith16Symbols() {
        Collection<Player> listBefore = service.getPlayers();
        assertThat(listBefore.size()).isEqualTo(0);

        String name = "Player1234567890";
        int renatId = service.createPlayer(name);
        Player playerById = service.getPlayerById(renatId);

        assertThat(playerById.getNick()).isEqualTo(name);
    }


    @Test
    @DisplayName("Проверка корректной загрузки json-файла")
    public void loadjsonFile() {
        helper.createListOfPlayers();
        assertThat(service.getPlayers().size()).isEqualTo(3);
        assertThat(service.getPlayerById(1).getNick()).isEqualTo("Player1");
        assertThat(service.getPlayerById(1).getPoints()).isEqualTo(1);
        assertThat(service.getPlayerById(2).getNick()).isEqualTo("Player2");
        assertThat(service.getPlayerById(2).getPoints()).isEqualTo(2);
        assertThat(service.getPlayerById(3).getNick()).isEqualTo("Player3");
        assertThat(service.getPlayerById(3).getPoints()).isEqualTo(3);
    }


    @Test
    @DisplayName("Нельзя загрузить json-файл с дубликатами")
    public void loadFileWithDuplicate() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> helper.getPlayersWithFile());
        assertThatIllegalArgumentException()
                .isThrownBy(() -> helper.addToFilePlayersWithFile());
    }


    @Test
    @DisplayName("Проверка сохранения в файл")
    public void checkSaveToFile() throws IOException {
        helper.createListOfPlayers();
        File file1 = Path.of("./fileForEquals.json").toFile();
        File file2 = Path.of("./data.json").toFile();
        boolean isTwoEqual = FileUtils.contentEquals(file1, file2);
        assertThat(isTwoEqual).isTrue();
    }


    @Test
    @DisplayName("Проверка загрузки невалидного json-файла")
    public void loadinInvalidFile() throws IOException {
        Files.deleteIfExists(Path.of("./data.json"));
        helper.doNewFile();
        new PlayerServiceImpl();
        assertThat(service.getPlayers().size()).isEqualTo(0);
    }

}


