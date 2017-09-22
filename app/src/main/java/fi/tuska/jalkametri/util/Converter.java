package fi.tuska.jalkametri.util;

/**
 * An interface for a converter that converts between two types.
 *
 * @author Tuukka Haapasalo
 *
 * @param <S> the source type
 * @param <T> the target type
 */
public interface Converter<S, T> {

    T convert(S src);

}
