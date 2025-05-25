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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.*;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


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
    private ImageView launchImage;


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

        launchTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getLinks() != null && newVal.getLinks().getPatch() != null) {
                String imageUrl = newVal.getLinks().getPatch().getSmall();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Image image = new Image(imageUrl, true); // true = carga en background
                    launchImage.setImage(image);
                } else {
                    launchImage.setImage(null);
                }
            }
        });

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

    @FXML
    public void exportRocketsToZip() {
        CompletableFuture.runAsync(() -> {
            try {
                // 1. Exportar a CSV
                File csvFile = new File("rockets.csv");
                try (PrintWriter writer = new PrintWriter(csvFile)) {
                    writer.println("Nombre,Primer Vuelo");
                    for (Rocket rocket : rocketList) {
                        writer.printf("%s,%s%n", rocket.getName(), rocket.getFirst_flight());
                    }
                }

                // 2. Comprimir en ZIP
                File zipFile = new File("rockets.zip");
                try (FileOutputStream fos = new FileOutputStream(zipFile);
                    ZipOutputStream zos = new ZipOutputStream(fos);
                    FileInputStream fis = new FileInputStream(csvFile)) {

                    ZipEntry entry = new ZipEntry(csvFile.getName());
                    zos.putNextEntry(entry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }

                // 3. Notificación en la interfaz
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Exportación completada: rockets.zip");
                    alert.showAndWait();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error al exportar: " + e.getMessage());
                    alert.showAndWait();
                });
            }
        });
    }

}
