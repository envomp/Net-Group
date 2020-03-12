package ee.suveulikool.netgroup.demo.domain;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueuePerson {

    private static final int MAX_DEPTH = 10;

    @NotNull
    private Person person;

    @Builder.Default
    @NotNull
    private int depth = MAX_DEPTH;

    private Person origin;

    private Direction direction;

    public enum Direction {
        UP,
        DOWN
    }
}
