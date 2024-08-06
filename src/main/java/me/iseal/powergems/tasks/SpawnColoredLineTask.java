package me.iseal.powergems.tasks;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class SpawnColoredLineTask extends BukkitRunnable {

    public Player spawningPlayer;
    public int lineRed;
    public int lineGreen;
    public int lineBlue;
    public int circleRed;
    public int circleGreen;
    public int circleBlue;
    public Location start;
    public Location target;
    public Consumer<Location> lineConsumer;
    public Consumer<Location> circleConsumer;

    public double lineInterval;
    public double circleInterval;
    public double circleParticleInterval;
    public double circleRadius;
    public boolean spawnCircles;
    public boolean spawnLines;

    private Location currentLineLocation;
    private Location currentCircleLocation;

    private double lineDistance;
    private Vector lineDirection;
    private double circleDistance;
    private Vector circleDirection;

    private double lineRun = 0;
    private double circleRun = 0;


    public void init() {
        if (spawnLines) {
            lineDirection = target.toVector().subtract(start.toVector()).normalize();
            lineDistance = start.distance(target);
            currentLineLocation = start.clone();
            circleInterval = circleInterval+circleRadius*2;
        }
        if (spawnCircles) {
            circleDirection = target.toVector().subtract(start.toVector()).normalize();
            circleDistance = start.distance(target);
            currentCircleLocation = start.clone();
        }
    }

    @Override
    public void run() {
        boolean doneLine = false;
        boolean doneCircle = false;
        if (spawnLines && lineRun <= lineDistance) {
            lineRun += lineInterval;
            currentLineLocation = start.clone().add(lineDirection.clone().multiply(lineRun));
            spawnLine();
        } else {
            doneLine = true;
        }
        if (spawnCircles && lineRun - circleRun >= circleInterval) {
            circleRun = lineRun;
            currentCircleLocation = start.clone().add(circleDirection.clone().multiply(circleRun));
            spawnCircle();
        } else {
            doneCircle = true;
        }
        if (doneLine && doneCircle) {
            cancel();
        }
    }
    
    private void spawnLine() {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(lineRed, lineGreen, lineBlue), 1);
        spawningPlayer.spawnParticle(
                Particle.REDSTONE,
                currentLineLocation,
                5,
                dustOptions);
        lineConsumer.accept(currentLineLocation);
    }
    
    private void spawnCircle() {
        for (double phi = 0; phi < Math.PI; phi += circleParticleInterval) {
            for (double theta = 0; theta < 2 * Math.PI; theta += circleParticleInterval) {
                double x = circleRadius * Math.sin(phi) * Math.cos(theta);
                double y = circleRadius * Math.sin(phi) * Math.sin(theta);
                double z = circleRadius * Math.cos(phi);
                Location particleLocation = currentCircleLocation.clone().add(x, y, z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(circleRed, circleGreen, circleBlue), 1);
                spawningPlayer.spawnParticle(
                        Particle.REDSTONE,
                        particleLocation,
                        5,
                        dustOptions);
            }
        }
        circleConsumer.accept(currentCircleLocation);
    }
    
}
