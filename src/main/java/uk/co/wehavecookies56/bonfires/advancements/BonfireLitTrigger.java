package uk.co.wehavecookies56.bonfires.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import uk.co.wehavecookies56.bonfires.Bonfires;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BonfireLitTrigger implements ICriterionTrigger<BonfireLitTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(Bonfires.modid, "bonfire_lit");
    private final Map<PlayerAdvancements, Listeners> listeners = Maps.newHashMap();

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull ICriterionTrigger.Listener<BonfireLitTrigger.Instance> listener) {
        BonfireLitTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);
        if (listeners == null) {
            listeners = new BonfireLitTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull ICriterionTrigger.Listener<BonfireLitTrigger.Instance> listener) {
        BonfireLitTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    @Override
    public void removeAllListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    @Nonnull
    public BonfireLitTrigger.Instance deserializeInstance(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
        return new BonfireLitTrigger.Instance();
    }

    public void trigger(EntityPlayerMP playerMP) {
        BonfireLitTrigger.Listeners listeners = this.listeners.get(playerMP.getAdvancements());
        if (listeners != null) {
            listeners.trigger();
        }
    }

    static class Instance extends AbstractCriterionInstance {

        Instance() {
            super(BonfireLitTrigger.ID);
        }

        boolean test() {
            return true;
        }

    }

    static class Listeners {

        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<Instance>> listeners = Sets.newHashSet();

        Listeners(PlayerAdvancements playerAdvancements) {
            this.playerAdvancements = playerAdvancements;
        }

        boolean isEmpty() {
            return listeners.isEmpty();
        }

        void add(ICriterionTrigger.Listener<BonfireLitTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        void remove(ICriterionTrigger.Listener<BonfireLitTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger() {
            List<Listener<Instance>> list = null;

            for (ICriterionTrigger.Listener<BonfireLitTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test()) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }
                    list.add(listener);
                }
            }
            if (list != null) {
                for (ICriterionTrigger.Listener<BonfireLitTrigger.Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
