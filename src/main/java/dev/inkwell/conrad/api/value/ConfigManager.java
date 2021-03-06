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

package dev.inkwell.conrad.api.value;

import dev.inkwell.conrad.api.value.data.Constraint;
import dev.inkwell.conrad.api.value.data.DataType;
import dev.inkwell.conrad.api.value.lang.Translator;
import dev.inkwell.conrad.api.value.util.ListView;
import dev.inkwell.conrad.impl.ConfigManagerImpl;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public interface ConfigManager {

    /**
     * @return all registered config definitions
     */
    static ListView<ConfigDefinition<?>> getConfigKeys() {
        return ConfigManagerImpl.getConfigKeys();
    }

    /**
     * @param configDefinition the config file whose values we want
     * @return a list of value keys associated with the config file
     */
    static ListView<ValueKey<?>> getValues(ConfigDefinition<?> configDefinition) {
        return ConfigManagerImpl.getValues(configDefinition);
    }

    /**
     * @param configKeyString the path of a config value key
     * @return the value key associated with that path if it exists, null otherwise
     */
    static @Nullable ValueKey<?> getValue(String configKeyString) {
        return ConfigManagerImpl.getValue(configKeyString);
    }

    /**
     * Saves the config definition to disk with values from the specified value container.
     *
     * @param config         the config file to save
     * @param valueContainer the value container where values are stored
     */
    static void save(ConfigDefinition<?> config, ValueContainer valueContainer) {
        ConfigManagerImpl.save(config, valueContainer);
    }

    /**
     * @param configKeyString the path of a config definition
     * @return the config definition if it exists, null otherwise
     */
    static <R> @Nullable ConfigDefinition<R> getDefinition(String configKeyString) {
        return ConfigManagerImpl.getDefinition(configKeyString);
    }

    /**
     * A utility method for getting all comments for a config value, including data, flags, and constraints.
     *
     * @param value the config value whose comments we want to get
     * @return an ordered collection of comment strings
     */
    static Collection<String> getComments(ValueKey<?> value) {
        Collection<String> comments = new ArrayList<>();

        DataType.COMMENT.addLines(value.getData(DataType.COMMENT), comments::add);

        List<String> dataStrings = new ArrayList<>();
        value.getDataTypes().forEach(dataType -> {
            if (dataType != DataType.COMMENT) {
                process(dataType, value, dataStrings::add);
            }
        });

        if (comments.size() > 0 && dataStrings.size() > 0) {
            comments.add("");
        }

        if (dataStrings.size() > 0) {
            comments.add(Translator.translate("conrad.data"));
            dataStrings.forEach(string -> comments.add("  " + string));
        }

        List<String> flagStrings = new ArrayList<>();
        value.getFlags().forEach(flag -> flag.addLines(flagStrings::add));

        if ((comments.size() > 0 || dataStrings.size() > 0) && flagStrings.size() > 0) {
            comments.add("");
        }

        if (flagStrings.size() > 0) {
            comments.add(Translator.translate("conrad.flags"));
            flagStrings.forEach(string -> comments.add("  " + string));
        }

        List<String> constraintStrings = new ArrayList<>();
        List<String> keyConstraintStrings = new ArrayList<>();
        value.getConstraints().forEach(constraint ->
                constraint.addLines((constraint instanceof Constraint.Key
                        ? keyConstraintStrings
                        : constraintStrings)::add)
        );

        if ((flagStrings.size() > 0 && constraintStrings.size() + keyConstraintStrings.size() > 0)
                || (constraintStrings.size() + keyConstraintStrings.size() > 0 && comments.size() > 0)) {
            comments.add("");
        }

        if (constraintStrings.size() + keyConstraintStrings.size() > 0) {
            comments.add(Translator.translate("conrad.constraints"));
            constraintStrings.forEach(string -> comments.add("  " + string));

            if (constraintStrings.size() > 0 && keyConstraintStrings.size() > 0) {
                comments.add("");
                comments.add(Translator.translate("conrad.key_constraints"));
            }

            keyConstraintStrings.forEach(string -> comments.add("  " + string));
        }

        return comments;
    }

    static <T> void process(DataType<T> dataType, ValueKey<?> valueKey, Consumer<String> linesConsumer) {
        dataType.addLines(valueKey.getData(dataType), linesConsumer);
    }
}
