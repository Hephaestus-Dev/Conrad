package dev.hephaestus.clothy.impl.gui.entries;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import dev.hephaestus.math.impl.Color;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class SelectionListEntry<T> extends TooltipListEntry<T> {
    private ImmutableList<T> values;
    private AtomicInteger index;
    private final int original;
    private ButtonWidget buttonWidget, resetButton;
    private List<Element> widgets;
    private Function<T, Text> nameProvider;
    
    public SelectionListEntry(Text fieldName, T[] valuesArray, T value, Text resetButtonKey, Supplier<T> defaultValue, Consumer<T> saveConsumer, Function<T, Text> nameProvider, @Nullable Function<T, Optional<List<Text>>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart, saveConsumer, defaultValue);

        if (valuesArray != null) {
            this.values = ImmutableList.copyOf(valuesArray);
        } else {
            this.values = ImmutableList.of(value);
        }

        this.index = new AtomicInteger(this.values.indexOf(value));
        this.index.compareAndSet(-1, 0);
        this.original = this.values.indexOf(value);
        this.buttonWidget = new ButtonWidget(0, 0, 150, 20, NarratorManager.EMPTY, widget -> {
            SelectionListEntry.this.index.incrementAndGet();
            SelectionListEntry.this.index.compareAndSet(SelectionListEntry.this.values.size(), 0);
        });
        this.resetButton = new ButtonWidget(0, 0, MinecraftClient.getInstance().textRenderer.getWidth(resetButtonKey) + 6, 20, resetButtonKey, widget -> {
            SelectionListEntry.this.index.set(getDefaultIndex());
        });
        this.widgets = Lists.newArrayList(buttonWidget, resetButton);
        this.nameProvider = nameProvider == null ? (t -> new TranslatableText(t instanceof Translatable ? ((Translatable) t).getCode() : t.toString())) : nameProvider;
    }
    
    @Override
    public boolean isEdited() {
        return super.isEdited() || !Objects.equals(this.index.get(), this.original);
    }
    
    @Override
    public T getValue() {
        return this.values.get(this.index.get());
    }
    
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = MinecraftClient.getInstance().getWindow();
        this.resetButton.active = isEditable() && getDefaultValue().isPresent() && getDefaultIndex() != this.index.get();
        this.resetButton.y = y;
        this.buttonWidget.active = isEditable();
        this.buttonWidget.y = y;
        this.buttonWidget.setMessage(nameProvider.apply(getValue()));
        Text displayedFieldName = getDisplayedFieldName();
        if (MinecraftClient.getInstance().textRenderer.isRightToLeft()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), window.getScaledWidth() - x - MinecraftClient.getInstance().textRenderer.getWidth(displayedFieldName), y + 6, getPreferredTextColor());
            this.resetButton.x = x;
            this.buttonWidget.x = x + resetButton.getWidth() + 2;
        } else {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, displayedFieldName.asOrderedText(), x, y + 6, getPreferredTextColor());
            this.resetButton.x = x + entryWidth - resetButton.getWidth();
            this.buttonWidget.x = x + entryWidth - 150;
        }
        this.buttonWidget.setWidth(150 - resetButton.getWidth() - 2);
        resetButton.render(matrices, mouseX, mouseY, delta);
        buttonWidget.render(matrices, mouseX, mouseY, delta);
    }
    
    private int getDefaultIndex() {
        return Math.max(0, this.values.indexOf(this.getDefaultValue().get()));
    }
    
    @Override
    public List<? extends Element> children() {
        return widgets;
    }
    
    public interface Translatable {
        @NotNull String getCode();
    }
    
}