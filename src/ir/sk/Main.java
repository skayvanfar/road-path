package ir.sk;

import java.util.*;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> on 10/21/2020.
 */
public class Main {

    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        IOConsole ioConsole = new IOConsole(input);
        Repository repository = new Repository(input);

        int choice;
        while (true) {
            choice = ioConsole.menuSelect();
            int result;
            switch (choice) {
                case 1:
                    ioConsole.help();
                    break;
                case 2:
                    result = ioConsole.itemSelect();
                    do {
                        if (result == 1) {
                            repository.addCity();
                            result = ioConsole.anotherItemSelect(ItemType.CITY);
                        } else {
                            repository.addRoad();
                            result = (ioConsole.anotherItemSelect(ItemType.ROAD) == 1)? 2: 0;
                        }

                        if (result == 1 || result == 2)
                            continue;
                        else
                            break;
                    } while (result == 1 || result == 2);

                    break;
                case 3:
                    result = ioConsole.itemSelect();
                    if (result == 1)
                        repository.deleteCity();
                    else
                        repository.deleteRoad();
                    break;
                case 4:
                    repository.path();
                    break;
                case 5:
                    return;
            }
        }
    }

}

class IOConsole {
    private Scanner input;

    public IOConsole(Scanner input) {
        this.input = input;
    }

    public void help() {
        System.out.println("Select a number from shown menu and enter. For example 1 is for help.");
    }

    public int menuSelect() {
        int c = 0;

        boolean validation = true;
        do {
            System.out.printf("com.company.Main Menu - Select an action:\n");
            System.out.printf("1. Help\n");
            System.out.printf("2. Add\n");
            System.out.printf("3. Delete\n");
            System.out.printf("4. Path\n");
            System.out.printf("5. Exit\n");

            String numberRegex = "[1-5]";
            String response = input.next();
            if (!response.matches(numberRegex))
                validation = false;
            else
                c = Integer.parseInt(response);

            if (!validation)
                System.out.println("Invalid input. Please enter 1 for more info.");
        } while (!validation);
        return c;
    }

    public int itemSelect() {
        int c;
        do {
            System.out.printf("Select model:\n");
            System.out.printf("1. com.company.City:\n");
            System.out.printf("2. com.company.Road:\n");
            c = input.nextInt();
        } while (c < 1 || c > 2);
        return c;
    }

    public int anotherItemSelect(ItemType itemType) {
        int c;
        do {
            System.out.printf("Select your next action\n");
            System.out.printf("1. Add another %s\n", itemType);
            System.out.printf("2. com.company.Main Menu\n");
            c = input.nextInt();
        } while (c < 1 || c > 2);
        return c;
    }
}

/**
 * a class that holds objects in memory and manages them
 */
class Repository {

    private Scanner input;

    // using hashtable for O(1) time complexity search and prevention from repeated city by the same id = key
    private Map<Integer, City> cities = new HashMap<>();
    // using Set to prevent from repeated road by the same id and O(1) time complexity search
    private Set<Road> roads = new HashSet<>();

    public Repository(Scanner input) {
        this.input = input;
        //  samples
        //  cities.put(1, new com.company.City(1, "Tehran"));
        //  cities.put(2, new com.company.City(2, "Karaj"));
        //  cities.put(3, new com.company.City(3, "Qom"));
    }

    public void addCity() {
        City city = new City();
        System.out.println("id:?");
        int id = input.nextInt();
        city.setId(id);

        System.out.println("name:?");
        city.setName(input.next());

        cities.put(id, city);

        System.out.printf("com.company.City with id=%d added!\n", id);
    }

    public void addRoad() {
        Road road = new Road();
        System.out.println("id:?");
        road.setId(Integer.parseInt(input.next()));

        System.out.println("name:?");
        road.setName(input.next());

        System.out.println("from:?");
        int fromId = Integer.parseInt(input.next());
        road.setFrom(fromId);

        System.out.println("to:?");
        int toId = Integer.parseInt(input.next());
        road.setTo(toId);

        System.out.println("through:?");
        input.nextLine();
        String path = input.nextLine();
        road.setThrough(path);

        System.out.println("speed_limit:?");
        road.setSpeedLimit(Integer.parseInt(input.next()));

        System.out.println("length:?");
        road.setLength(Integer.parseInt(input.next()));

        System.out.println("bi_directional:?");
        road.setBiDirectional(Integer.parseInt(input.next()));

        roads.add(road);
    }

    public void deleteCity() {
        int id = input.nextInt();
        if (!cities.containsKey(id)) {
            System.out.printf("com.company.City with id %d not found!\n", id);
            return;
        }

        cities.remove(id);
        roads.stream().filter(road -> road.getThrough().contains(id))
                .forEach(road -> road.getThrough().remove(id));
        System.out.printf("com.company.City:%d deleted!\n", id);
    }

    public void deleteRoad() {
        int id = input.nextInt();
        if (!roads.stream().filter(o -> o.getId() == id).findFirst().isPresent()) {
            System.out.printf("com.company.Road with id %d not found!\n", id);
            return;
        }
        roads.removeIf(o -> o.getId() == id);
        System.out.printf("com.company.Road:%d deleted!\n", id);
    }

    public void path() {
        String path = input.next();
        String[] sourceDestIds = path.split(":");
        Integer sourceCityId = Integer.valueOf(sourceDestIds[0]);
        Integer destCityId = Integer.valueOf(sourceDestIds[1]);
        roads.stream().filter(road -> road.isPathInRoad(sourceCityId, destCityId)).forEach(road -> {
            System.out.printf("%s:%s via com.company.Road %s: Takes %s\n", cities.get(sourceCityId), cities.get(destCityId), road, road.calculateTime());
        });
    }

}

enum ItemType {
    CITY, ROAD;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}

class City {
    private int id;
    private String name;

    public City() {
    }

    public City(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return id == city.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}

class Road {
    private int id;
    private String name;
    private int from;
    private int to;
    private List<Integer> through;
    private int speedLimit;
    private int length;
    private boolean biDirectional;

    public Road() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public List<Integer> getThrough() {
        return through;
    }

    public void setThrough(List<Integer> through) {
        this.through = through;
    }

    /**
     * set path by this string pattern: [1,2,3]
     *
     * @param path
     */
    public void setThrough(String path) {
        path = path.substring(1, path.length() - 1);
        String[] citiesInPath = path.split(",");
        Integer[] array = Arrays.asList(citiesInPath).stream().mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
        setThrough(Arrays.asList(array));
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isBiDirectional() {
        return biDirectional;
    }

    public void setBiDirectional(boolean biDirectional) {
        this.biDirectional = biDirectional;
    }

    /**
     * in case biDirectional is 1 set true, otherwise false
     * @param biDirectional
     */
    public void setBiDirectional(int biDirectional) {
        this.setBiDirectional(biDirectional == 1 ? true : false);
    }

    /**
     * check if this road includes two cities by their ids
     *
     * @param sourceCityId
     * @param destCityId
     * @return
     */
    public boolean isPathInRoad(int sourceCityId, int destCityId) {
        return (this.getThrough().contains(sourceCityId) && this.getThrough().contains(destCityId)
                && this.getThrough().indexOf(sourceCityId) <= this.getThrough().indexOf(destCityId))
                || ((this.getThrough().contains(sourceCityId) && this.getThrough().contains(destCityId) && this.isBiDirectional()));
    }

    public String calculateTime() {
        int time = this.getLength() * 60 / this.getSpeedLimit();
        return String.format("%02d", time / 24 / 60) + ":" + String.format("%02d", time / 60 % 24) + ':' + String.format("%02d", time % 60);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Road road = (Road) o;
        return id == road.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name;
    }
}

