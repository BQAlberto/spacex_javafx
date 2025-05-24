package com.svalero.spacex.network;

import com.svalero.spacex.model.Launch;
import com.svalero.spacex.model.Rocket;

import java.util.List;
import io.reactivex.Observable;
import retrofit2.http.GET;

public interface SpaceXApi {
    @GET("v4/launches")
    Observable<List<Launch>> getLaunches();

    @GET("v4/rockets")
    Observable<List<Rocket>> getRockets();

}
