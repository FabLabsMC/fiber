import me.zeroeightsix.fiber.annotations.Comment;
import me.zeroeightsix.fiber.annotations.Constrain;

public class Pojo {

    @Constrain.Min(5)
    private final int a = 10;

    @Comment("Test comment")
    private final String b = "Hello world";

}
