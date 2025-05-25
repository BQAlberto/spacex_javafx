package com.svalero.spacex.model;

public class Launch {
    private String name;
    private String date_utc;
    private Links links;

    public String getName() {
        return name;
    }

    public String getDate_utc() {
        return date_utc;
    }

    public Links getLinks() {
        return links;
    }

    public static class Links {
        private Patch patch;

        public Patch getPatch() {
            return patch;
        }
    }

    public static class Patch {
        private String small;

        public String getSmall() {
            return small;
        }
    }

}
