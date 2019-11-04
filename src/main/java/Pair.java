import lombok.Value;

@Value(staticConstructor = "of")
public class Pair<F, S> {
    private F first;
    private S second;
}
