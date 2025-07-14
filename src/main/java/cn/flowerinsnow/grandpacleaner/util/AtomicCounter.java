/* Copyright (C) 2025 flowerinsnow
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.flowerinsnow.grandpacleaner.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Consumer;

public abstract class AtomicCounter {
    private @Range(from = 0L, to = Integer.MAX_VALUE) int counter = 0;
    private @Range(from = 0L, to = Integer.MAX_VALUE) int total = 0;
    private @Range(from = 0L, to = Integer.MAX_VALUE) int extraCounter = 0;
    private boolean submitOver;
    private boolean allOver;

    @Contract("_ -> new")
    public static @NotNull AtomicCounter create(@NotNull Consumer<AtomicCounter> onOver) {
        return new AtomicCounter() {
            @Override
            protected void onAllOver() {
                onOver.accept(this);
            }
        };
    }

    public synchronized int countUp() {
        if (this.isAllOver()) {
            throw new IllegalStateException("counter is already all-over");
        }
        this.total++;
        return ++this.counter;
    }

    public synchronized int countDown() {
        if (this.counter > 0) {
            this.counter--;
            if (this.counter == 0 && this.isSubmitOver()) {
                this.allOver = true;
                this.onAllOver();
            }
        } else { // <= 0
            throw new IndexOutOfBoundsException();
        }
        return this.counter;
    }

    @Contract(pure = true)
    public synchronized @Range(from = 0L, to = Integer.MAX_VALUE) int counter() {
        return this.counter;
    }

    @Contract(pure = true)
    public synchronized @Range(from = 0L, to = Integer.MAX_VALUE) int total() {
        return this.total;
    }

    @Contract(pure = true)
    public synchronized @Range(from = 0L, to = Integer.MAX_VALUE) int extraCounter() {
        return this.extraCounter;
    }

    public synchronized @Range(from = 0L, to = Integer.MAX_VALUE) int extraCountUp() {
        return ++this.extraCounter;
    }

    public synchronized @Range(from = 0L, to = Integer.MAX_VALUE) int extraCountUp(@Range(from = 1L, to = Integer.MAX_VALUE) int count) {
        return this.extraCounter += count;
    }

    @Contract(pure = true)
    public synchronized boolean isSubmitOver() {
        return this.submitOver;
    }

    @Contract("-> this")
    public synchronized @NotNull AtomicCounter submitOver() {
        this.submitOver = true;
        return this;
    }

    @Contract(pure = true)
    public synchronized boolean isAllOver() {
        return this.allOver;
    }

    @Contract("-> this")
    public synchronized @NotNull AtomicCounter allOver() {
        this.allOver = true;
        return this;
    }

    protected abstract void onAllOver();
}
