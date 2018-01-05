import java.util.ArrayList;

/**
 * Created by Gandi on 09/12/2017.
 */
public class Collisions {

    public static boolean checkIfInRange(double a, double b) {
        double tolerance = 2d;
        double min = b - tolerance;
        double max = b + tolerance;
        if (a > min && a < max) {
            return true;
        }
        return false;
    }

    public static int detectCollision(Pedestrian Apedestrian, Pedestrian Bpedestrian) {
        Position positionA = Apedestrian.position;
        Position positionB = Bpedestrian.position;
        if (checkIfInRange(positionA.x, positionB.x) || checkIfInRange(positionA.y, positionB.y)) {
            return 1;
        }
        return 2;
    }

    public static boolean findNeighbours(Pedestrian[] pedestriansOnMap, Position newPosition, Pedestrian currentPedestrian) {
        int sasiedzi = 0;
        int stop = 0;
        boolean needToStopPedestrian = false;
        for (int i = 0; i < pedestriansOnMap.length; i++) {
            if ((currentPedestrian.position.y != pedestriansOnMap[i].position.y) ||
                    currentPedestrian.position.x != pedestriansOnMap[i].position.x) {
                double distanceBetweenPedestrians = Distance.calculateDistance(newPosition, pedestriansOnMap[i].position, false);
                double radiusSum = 8;
                distanceBetweenPedestrians /= Constants.mapScale;
                radiusSum /= Constants.mapScale;
                if (distanceBetweenPedestrians <= radiusSum) {
                    int collisionNumber = detectCollision(currentPedestrian, pedestriansOnMap[i]);
                    currentPedestrian.attempt++;
                    if (collisionNumber == 1) {
                        // back collision
                        double velocity1 = vector2d.calculateVectorMagnitude(pedestriansOnMap[i].getVelocity());
                        double velocity2 = vector2d.calculateVectorMagnitude(currentPedestrian.getVelocity());
                        if (velocity2 > velocity1) {
                            currentPedestrian.setVelocity(pedestriansOnMap[i].getVelocity());
                        } else if (currentPedestrian.attempt < 1) {
                            currentPedestrian.setVelocity(pedestriansOnMap[i].getVelocity());
                        } else {
                            currentPedestrian.attempt = 0;
                            needToStopPedestrian = true;
                        }
                        System.out.println("back");
                    } else if (collisionNumber == 2) {
                        // side collision
                        System.out.println("side");
                        double velocity1 = vector2d.calculateVectorMagnitude(pedestriansOnMap[i].getVelocity());
                        double velocity2 = vector2d.calculateVectorMagnitude(currentPedestrian.getVelocity());
                        if (velocity1 > velocity2) {
                            vector2d newVelocity = new vector2d(currentPedestrian.getVelocity().getX() * -1.5d, currentPedestrian.getVelocity().getY() * -0.5d);
                            currentPedestrian.setVelocity(newVelocity);
                        } else {
                            needToStopPedestrian = true;
                        }
                    }
                    sasiedzi++;
                    if (pedestriansOnMap[i].stopped) {
                        stop++;
                    }
                } else {
                    // no collision
                }
            }
        }
        if (needToStopPedestrian && sasiedzi != stop) {
            return true;
        }
        return false;
    }

    public static void avoid(Map map, Pedestrian currentPedestrian, Position newPosition) {
        currentPedestrian.stopped = findNeighbours(map.pedestrians, newPosition, currentPedestrian);
    }
}
