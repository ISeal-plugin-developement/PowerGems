package dev.iseal.powergems.tasks;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class SpawnColoredLineTask implements Runnable {
    // Color settings
    public int lineRed = 255, lineGreen = 255, lineBlue = 255;
    public int circleRed = 255, circleGreen = 255, circleBlue = 255;
    
    // Location settings
    public Location start = null;
    public Location target = null;
    public Consumer<Location> lineConsumer = loc -> {};
    public Consumer<Location> circleConsumer = loc -> {};

    // Interval settings
    public double lineInterval = 0.1;
    public double circleInterval = 1.0;
    public double circleParticleInterval = 0.1;
    public double circleRadius = 1.0;
    
    // State flags
    public boolean spawnCircles = false;
    public boolean spawnLines = false;
    public boolean persistent = false;
    public int repeatAmount = 1;

    // Internal state
    private Location currentLineLocation = null;
    private Location currentCircleLocation = null;
    private final ArrayList<Location> lineLocations = new ArrayList<>();
    private final ArrayList<Location> circleLocations = new ArrayList<>();
    private double lineDistance = 0;
    private Vector lineDirection = new Vector(0, 0, 0);
    private double circleDistance = 0;
    private Vector circleDirection = new Vector(0, 0, 0);
    private double lineRun = 0;
    private double circleRun = 0;
    private int counter = 0;

    public void init() {
        if (spawnLines) {
            lineDirection = target.toVector().subtract(start.toVector()).normalize();
            lineDistance = start.distance(target);
            currentLineLocation = start.clone();
            circleInterval = circleInterval + circleRadius * 2;
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
        
        for (int i = 0; i < repeatAmount; i++) {
            if (spawnLines && lineRun <= lineDistance) {
                lineRun += lineInterval;
                currentLineLocation = start.clone().add(lineDirection.clone().multiply(lineRun));
                if (counter % 2 == 0) {
                    lineLocations.add(currentLineLocation.clone());
                }
                spawnLine();
            } else {
                doneLine = true;
            }
            
            if (spawnCircles && lineRun - circleRun >= circleInterval) {
                circleRun = lineRun;
                currentCircleLocation = start.clone().add(circleDirection.clone().multiply(circleRun));
                circleLocations.add(currentCircleLocation.clone());
                spawnCircle();
            } else {
                doneCircle = true;
            }
            
            counter++;
            if (persistent && counter % 20 == 0) {
                refreshParticles();
            }
            
            if (doneLine && doneCircle) {
                return;
            }
        }
    }
    
    private void refreshParticles() {
        counter = 0;
        Location oldLineLocation = currentLineLocation.clone();
        Location oldCircleLocation = currentCircleLocation.clone();
        
        lineLocations.forEach(loc -> {
            currentLineLocation = loc;
            spawnLine();
            lineConsumer.accept(loc);
        });
        
        circleLocations.forEach(loc -> {
            currentCircleLocation = loc;
            spawnCircle();
            circleConsumer.accept(loc);
        });
        
        currentLineLocation = oldLineLocation;
        currentCircleLocation = oldCircleLocation;
    }
    
    private void spawnLine() {
        World world = currentLineLocation.getWorld();
        Particle.DustOptions dustOptions = new Particle.DustOptions(
            Color.fromRGB(lineRed, lineGreen, lineBlue), 1
        );
        world.spawnParticle(
            Particle.REDSTONE,
            currentLineLocation,
            5,
            0, 0, 0,  // No spread
            0,        // No extra data
            dustOptions,
            true     // Force display
        );
        lineConsumer.accept(currentLineLocation);
    }
    
    private void spawnCircle() {
        World world = currentCircleLocation.getWorld();
        Particle.DustOptions dustOptions = new Particle.DustOptions(
            Color.fromRGB(circleRed, circleGreen, circleBlue), 1
        );
        
        for (double phi = 0; phi < Math.PI; phi += circleParticleInterval) {
            for (double theta = 0; theta < 2 * Math.PI; theta += circleParticleInterval) {
                double x = circleRadius * Math.sin(phi) * Math.cos(theta);
                double y = circleRadius * Math.sin(phi) * Math.sin(theta);
                double z = circleRadius * Math.cos(phi);
                Location particleLocation = currentCircleLocation.clone().add(x, y, z);
                
                world.spawnParticle(
                    Particle.REDSTONE,
                    particleLocation,
                    5,
                    0, 0, 0,  
                    0,        
                    dustOptions,
                    true     
                );
            }
        }
        circleConsumer.accept(currentCircleLocation);
    }
}
