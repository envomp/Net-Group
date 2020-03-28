package ee.suveulikool.netgroup.demo.utils;

import ee.suveulikool.netgroup.demo.domain.Person;
import ee.suveulikool.netgroup.demo.domain.QueuePerson;

import java.util.ArrayList;
import java.util.LinkedList;

public class PersonUtils {

    public static void generateTreeWithPersonAsRoot(Person person, int depth) {
        LinkedList<QueuePerson> queue = new LinkedList<>();
        queue.add(QueuePerson.builder().depth(depth).person(person).build());
        QueuePerson origin;

        while (queue.size() != 0) {
            origin = queue.poll();
            modifyOrigin(origin);

            if (origin.getDepth() != 0) { // break condition

                for (Person child : origin.getPerson().getChildren()) {
                    if (!child.isCut()) {
                        queue.add(QueuePerson.builder()
                                .depth(origin.getDepth() - 1)
                                .person(child)
                                .origin(origin.getPerson())
                                .direction(QueuePerson.Direction.UP)
                                .build());
                    }
                }

                for (Person parent : origin.getPerson().getParents()) {
                    if (!parent.isCut()) {
                        queue.add(QueuePerson.builder()
                                .depth(origin.getDepth() - 1)
                                .person(parent)
                                .origin(origin.getPerson())
                                .direction(QueuePerson.Direction.DOWN)
                                .build());
                    }
                }

            } else {
                origin.getPerson().setParents(new ArrayList<>());
                origin.getPerson().setChildren(new ArrayList<>());
            }


            if (origin.getOrigin() != null) {
                if (origin.getDirection() == QueuePerson.Direction.UP) {
                    origin.getPerson().getParents().remove(origin.getOrigin());
                } else {
                    origin.getPerson().getChildren().remove(origin.getOrigin());
                }
            }
        }
    }

    private static void modifyOrigin(QueuePerson origin) {
        origin.getPerson().setCut(true);
        origin.getPerson().fillPostTransactionFields(); // Fill fields for graph
    }

    public static Boolean isAncestor(Person target, Person root, int depth) {
        if (target == root) {
            return true;
        }
        if (depth == 0) {
            return false;
        }

        for (Person child : root.getParents()) {
            if (isAncestor(target, child, depth - 1)) {
                return true;
            }
        }

        return false;
    }

    public static Boolean isInATree(Person root, Person target, boolean turned, int depth) {
        if (depth == 0) {
            return false;
        }
        if (root == target) {
            return true;
        }
        root.setCut(true);
        if (!turned) {
            for (Person parent : root.getParents()) {
                if (!parent.isCut() && isInATree(parent, target, false, depth - 1)) {
                    root.setCut(false);
                    return true;
                }
            }
        }

        for (Person child : root.getChildren()) {
            if (!child.isCut() && isInATree(child, target, true, depth - 1)) {
                root.setCut(false);
                return true;
            }
        }

        root.setCut(false);
        return false;
    }

    public static Boolean IsRelative(Person root, Person person, int depth) {
        root.setCut(true);
        boolean answer = false;
        for (Person parent : root.getParents()) {
            if (isInATree(parent, person, false, depth)) {
                answer = true;
                break;
            }
        }

        for (Person child : root.getChildren()) {
            if (isInATree(child, person, true, depth)) {
                answer = true;
                break;
            }
        }
        root.setCut(false);
        return answer;
    }

}
