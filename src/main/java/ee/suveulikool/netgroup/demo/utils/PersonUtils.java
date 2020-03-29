package ee.suveulikool.netgroup.demo.utils;

import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.domain.QueuePerson;

import java.util.ArrayList;
import java.util.LinkedList;

public class PersonUtils {

    public static void generateGraphWithPersonAsRoot(Person person, int depth) {
        LinkedList<QueuePerson> queue = new LinkedList<>();
        queue.add(QueuePerson.builder().depth(depth).person(person).build());
        modifyOrigin(person);
        QueuePerson origin; // BFS queue

        while (queue.size() != 0) {
            origin = queue.poll();
            modifyOrigin(origin.getPerson()); // Reason in modifying it here is because of concurrency.

            if (origin.getDepth() != 0) {

                for (Person child : origin.getPerson().getChildren()) {
                    if (!child.isCut()) {
                        queue.add(QueuePerson.builder()
                                .depth(origin.getDepth() - 1)
                                .person(child)
                                .origin(origin.getPerson())
                                .build());
                    }
                }

                for (Person parent : origin.getPerson().getParents()) {
                    if (!parent.isCut()) {
                        queue.add(QueuePerson.builder()
                                .depth(origin.getDepth() - 1)
                                .person(parent)
                                .origin(origin.getPerson())
                                .build());
                    }
                }

            } else { // break condition when depth was reached. Construct a leaf.
                Person finalOrigin = origin.getPerson();
                leafify(finalOrigin);
            }
        }
    }

    private static void leafify(Person finalOrigin) {
        finalOrigin.getParents().stream().filter(x -> !x.isCut())
                .forEach(x -> x.getChildren().remove(finalOrigin));
        finalOrigin.setParents(new ArrayList<>());
        finalOrigin.getChildren().stream().filter(x -> !x.isCut())
                .forEach(x -> x.getParents().remove(finalOrigin));
        finalOrigin.setChildren(new ArrayList<>());
    }

    public static void generateTreeWithPersonAsRoot(Person person, int depth) {
        if (depth == 0) {
            leafify(person);
            return;
        }
        for (Person parent : person.getParents()) {
            generateTreeWithPersonAsRoot(parent, depth - 1);
            for (Person child : parent.getChildren()) {
                if (person != child) {
                    child.setCut(true);
                    child.getParents().remove(parent);
                }
            }
            parent.setCut(true);
            parent.setChildren(new ArrayList<>());
        }
    }


    private static void modifyOrigin(Person origin) {
        origin.setCut(true);
        origin.fillPostTransactionFields(); // Fill fields for graph

        for (Person parent : origin.getParents()) { // Remove upper inbound links
            parent.getChildren().remove(origin);
        }
        for (Person child : origin.getChildren()) { // Remove lower inbound links
            child.getParents().remove(origin);
        }
    }

    public static Integer getNumberOfAncestor(Person root, int depth) {
        if (depth == 0) { // depth too big. Giving up
            return 1;
        }

        int cur = 1;

        for (Person parent : root.getParents()) {  // expand up
//            System.out.println("parent " + parent.getName() + getNumberOfAncestor(parent, depth - 1));
            cur += getNumberOfAncestor(parent, depth - 1);
        }

        return cur; // Tree was searched.
    }

    public static Boolean isAncestor(Person target, Person root, int depth) {
        if (target == root) { // ancestor was found
            return true;
        }

        if (depth == 0) { // depth too big. Giving up
            return false;
        }

        for (Person parent : root.getParents()) {  // expand up
            if (isAncestor(target, parent, depth - 1)) {
                return true; // ancestor was found
            }
        }

        return false; // Tree was searched. Nothing found
    }

    public static Boolean isInATree(Person root, Person target, boolean turned, int depth) {

        if (root == target) { // blood relative was found
            return true;
        }

        if (depth == 0) { // depth too big. Giving up
            return false;
        }

        root.setCut(true); // stop infinite loop
        if (!turned) { // only 1 turn can happen. Otherwise father and mother would be relatives without incest
            for (Person parent : root.getParents()) { // expand up
                if (!parent.isCut() && isInATree(parent, target, false, depth - 1)) {
                    root.setCut(false); // inplace modification. Graph will be left unharmed
                    return true; // blood relative was found
                }
            }
        }

        for (Person child : root.getChildren()) { // expand down
            if (!child.isCut() && isInATree(child, target, true, depth - 1)) {
                root.setCut(false); // inplace modification. Graph will be left unharmed
                return true; // blood relative was found
            }
        }

        root.setCut(false); // inplace modification. Graph will be left unharmed
        return false; // graph was searched. nothing found
    }

    public static Boolean IsRelative(Person root, Person person, int depth) { // Controller function
        root.setCut(true); // stop infinite loop
        boolean answer = false;

        for (Person parent : root.getParents()) {
            if (isInATree(parent, person, false, depth)) {
                answer = true;
                break;
            }
        }

        if (!answer) { // no need to keep looking
            for (Person child : root.getChildren()) {
                if (isInATree(child, person, true, depth)) {
                    answer = true;
                    break;
                }
            }
        }

        root.setCut(false); // inplace modification. Graph will be left unharmed
        return answer;
    }

    public static Boolean isPotentialUncleOrAunt(Person person) {
        for (Person parent : person.getParents()) {
            for (Person child : parent.getChildren()) {
                for (Person grand : child.getChildren()) {
                    return true;
                }
            }
        }

        return false;
    }

}
