/*
 * Copyright 2021 Haven King
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.inkwell.vivid.api.widgets.value.entry;

import dev.inkwell.vivid.api.screen.ConfigScreen;
import dev.inkwell.vivid.api.util.Alignment;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LongEntryWidget extends NumberEntryWidget<Long> {
    public LongEntryWidget(ConfigScreen parent, int x, int y, int width, int height, Alignment alignment, Supplier<@NotNull Long> defaultValueSupplier, Consumer<Long> changedListener, Consumer<Long> saveConsumer, @NotNull Long value) {
        super(parent, x, y, width, height, alignment, defaultValueSupplier, changedListener, saveConsumer, value);
        this.setTextPredicate(string -> {
            try {
                Long.parseLong(string);
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        });
    }

    @Override
    protected String valueOf(Long value) {
        return String.valueOf(value);
    }

    @Override
    protected Long emptyValue() {
        return 0L;
    }

    @Override
    protected Optional<Long> parse(String value) {
        try {
            return Optional.of(Long.valueOf(value));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public boolean isWithinBounds(Long value) {
        return (min == null || value >= min) && (max == null || value <= max);
    }
}
