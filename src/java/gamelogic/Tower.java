package gamelogic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import org.json.simple.JSONObject;

public class Tower extends Entity {

    protected String id;
    protected int countProjectile;
    protected boolean dead;
    protected int team;
    protected int health;
    protected int healthMax;

    public Tower(String id, int countProjectile, boolean dead, int team, int health, int healthMax, int x, int y, String name, boolean destroy) {
        super(x, y, name, destroy);
        this.id = id;
        this.countProjectile = countProjectile;
        this.dead = dead;
        this.team = team;
        this.health = health;
        this.healthMax = healthMax;
    }

    @Override
    public LinkedList<State> generate(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        LinkedList<State> newStates = new LinkedList<>();
        if (!dead) {
            Random random = new Random();
            if (random.nextInt(100) <= 10) {
                int xVelocity = 0;
                int yVelocity = 0;
                do {
                    xVelocity = random.nextInt(3) - 1;
                    yVelocity = random.nextInt(3) - 1;
                } while (xVelocity == 0 && yVelocity == 0);
                Projectile projectile = new Projectile(id, countProjectile, team, xVelocity, yVelocity, x, y, "Projectile", false);
                newStates.add(projectile);
                this.addEvent("fire");
            }
        }
        return newStates;
    }

    @Override
    public State next(LinkedList<State> states, LinkedList<StaticState> staticStates, HashMap<String, Action> actions) {
        hasChanged = false;
        int newCountProjectile = countProjectile;
        boolean newDead = dead;
        int newHealth = health;
        boolean newDestroy = destroy;
        LinkedList<String> events = getEvents();
        if (!events.isEmpty()) {
            hasChanged = true;
            for (String event : events) {
                switch (event) {
                    case "hit":
                        newHealth = health - 10;
                        if (newHealth <= 0) {
                            newDead = true;
                        }
                        System.out.println("Tower " + id + " has been killed.");
                        break;
                    case "fire":
                        newCountProjectile = countProjectile + 1;
                        //System.out.println("Tower " + id + " fired a projectile.");
                        break;
                    case "spawn":
                        System.out.println("Tower " + id + " spawn in game.");
                        break;
                    case "despawn":
                        newDestroy = true;
                        System.out.println("Tower " + id + " despawn of the game.");
                        break;
                }
            }
        }
        Tower newTower = new Tower(id, newCountProjectile, newDead, team, newHealth, healthMax, x, y, name, newDestroy
        );
        return newTower;
    }

    @Override
    public void setState(State newTower) {
        super.setState(newTower);
        id = ((Tower) newTower).id;
        countProjectile = ((Tower) newTower).countProjectile;
        dead = ((Tower) newTower).dead;
        team = ((Tower) newTower).team;
        health = ((Tower) newTower).health;
        healthMax = ((Tower) newTower).healthMax;
    }

    @Override
    protected Object clone() {
        Tower clon = new Tower(id, countProjectile, dead, team, health, healthMax, x, y, name, destroy);
        return clon;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonTower = new JSONObject();
        JSONObject jsonAttrs = new JSONObject();
        jsonAttrs.put("super", super.toJSON());
        jsonAttrs.put("id", id);
        jsonAttrs.put("countProjectile", countProjectile);
        jsonAttrs.put("dead", dead);
        jsonAttrs.put("team", team);
        jsonAttrs.put("health", health);
        jsonAttrs.put("healthMax", healthMax);
        jsonTower.put("Tower", jsonAttrs);
        return jsonTower;
    }

}
