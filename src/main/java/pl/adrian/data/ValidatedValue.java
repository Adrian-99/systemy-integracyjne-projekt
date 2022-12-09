package pl.adrian.data;

public record ValidatedValue(Long value, Integer moduloBase, boolean isValid) {
}
