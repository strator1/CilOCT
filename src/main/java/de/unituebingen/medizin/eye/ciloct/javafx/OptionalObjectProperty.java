package de.unituebingen.medizin.eye.ciloct.javafx;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author strasser
 * @param <T>
 */
public class OptionalObjectProperty<T> extends SimpleObjectProperty<Optional<T>> {
	public OptionalObjectProperty() {
		super(Optional.empty());
	}

	public OptionalObjectProperty(final T value) {
		super(Optional.of(value));
	}

	public void empty() {
		super.set(Optional.empty());
	}

	public T getActualValue() throws NoSuchElementException {
		return super.get().get();
	}

	public void setActualValue(final T value) {
		super.set(Optional.of(value));
	}

	public boolean isPresent() {
		return super.get().isPresent();
	}

	public void ifPresent(final Consumer<? super T> consumer) {
		super.get().ifPresent(consumer);
	}

	public <U> void andPresent(final Optional<U> optional, final BiConsumer<? super T, U> consumer) {
		optional.ifPresent(u -> this.ifPresent(t -> consumer.accept(t, u)));
	}

	public <U> void andPresent(final OptionalObjectProperty<U> optional, final BiConsumer<? super T, U> consumer) {
		optional.ifPresent(u -> this.ifPresent(t -> consumer.accept(t, u)));
	}

	public Optional<T> filter(final Predicate<? super T> predicate) {
		return super.get().filter(predicate);
	}

	public <U> Optional<U> map(final Function<? super T, ? extends U> mapper) {
		return super.get().map(mapper);
	}

	public <U> Optional<U> flatMap(final Function<? super T, Optional<U>> mapper) {
		return super.get().flatMap(mapper);
	}

	public T orElse(final T other) {
		return super.get().orElse(other);
	}

	public T orElseGet(final Supplier<? extends T> other) {
		return super.get().orElseGet(other);
	}

	public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
		return super.get().orElseThrow(exceptionSupplier);
	}
}