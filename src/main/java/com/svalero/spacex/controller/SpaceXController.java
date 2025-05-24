package com.svalero.spacex.controller;

import com.svalero.spacex.model.Launch;
import com.svalero.spacex.model.Rocket;
import com.svalero.spacex.network.RetrofitClient;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class SpaceXController {

    @FXML
    private TableView<Launch> launchTable;

    @FXML
    private TableColumn<Launch, String> nameColumn;

    @FXML
    private TableColumn<Launch, String> dateColumn;

    @FXML
    private TextField searchField;

    private ObservableList<Launch> launchList = FXCollections.observableArrayList();
    private FilteredList<Launch> filteredData;

    @FXML
    private TableView<Rocket> rocketTable;

    @FXML
    private TableColumn<Rocket, String> rocketNameColumn;

    @FXML
    private TableColumn<Rocket, String> firstFlightColumn;

    private ObservableList<Rocket> rocketList = FXCollections.observableArrayList();

    @FXML
    private TextField rocketSearchField;

    private FilteredList<Rocket> filteredRockets;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDate_utc()));

        rocketNameColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        firstFlightColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFirst_flight()));

        filteredData = new FilteredList<>(launchList, p -> true);
        launchTable.setItems(filteredData);

        rocketTable.setItems(rocketList);

        searchField.textProperty().addListener((obs, oldVal, newVal) ->
            filteredData.setPredicate(launch -> launch.getName().toLowerCase().contains(newVal.toLowerCase()))
        );

        loadLaunches();
        loadRockets();
    }

    @FXML
    public void loadLaunches() {
        RetrofitClient.getApi()
                .getLaunches()
                .subscribeOn(Schedulers.io())
                .observeOn(io.reactivex.schedulers.Schedulers.single()) // ejecutar fuera del hilo de JavaFX
                .subscribe(
                        launches -> Platform.runLater(() -> {
                            launchList.setAll(launches);
                        }),
                        error -> Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar datos: " + error.getMessage());
                            alert.showAndWait();
                        })
                );
    }
    
    public void loadRockets() {
        RetrofitClient.getApi()
                .getRockets()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                    rockets -> Platform.runLater(() -> {
                        rocketList.setAll(rockets);
                        filteredRockets = new FilteredList<>(rocketList, p -> true);
                        rocketTable.setItems(filteredRockets);

                        rocketSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
                            filteredRockets.setPredicate(rocket ->
                                    rocket.getName().toLowerCase().contains(newVal.toLowerCase().trim()));
                        });
                    }),
                    error -> Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Error al cargar cohetes: " + error.getMessage());
                        alert.showAndWait();
                    })
                );
    }

}
