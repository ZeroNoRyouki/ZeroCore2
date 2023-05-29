package it.zerono.mods.zerotest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Log {

    public static final Logger LOGGER = LogManager.getLogger(ZeroTest.MOD_ID);

    public static final Marker TEST = MarkerManager.getMarker("test");
}
