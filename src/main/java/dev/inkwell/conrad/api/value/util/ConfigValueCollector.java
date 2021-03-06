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

package dev.inkwell.conrad.api.value.util;

import dev.inkwell.conrad.api.value.ValueKey;
import org.jetbrains.annotations.NotNull;

public interface ConfigValueCollector {
    /**
     * Adds a config value to this builder
     *
     * @param valueKey the value to add
     * @param path0    the first element of the path
     * @param path     any additional elements of the path, for nested elements
     */
    void addConfigValue(@NotNull ValueKey<?> valueKey, @NotNull String path0, String... path);
}
