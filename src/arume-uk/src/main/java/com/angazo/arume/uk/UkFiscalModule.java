package com.angazo.arume.uk;

import java.util.Collection;
import java.util.List;

import com.angazo.arume.core.module.FiscalCapability;
import com.angazo.arume.core.module.FiscalModule;
import com.angazo.arume.core.module.FiscalModuleDescriptor;
import org.springframework.stereotype.Component;

/**
 * The United Kingdom module contributes jurisdiction data to the core catalogs and does not
 * own any table or fiscal capability yet.
 */
@Component
public final class UkFiscalModule implements FiscalModule {

    private final FiscalModuleDescriptor descriptor = UkModuleDescriptor.descriptor();

    @Override
    public FiscalModuleDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public Collection<? extends FiscalCapability> capabilities() {
        return List.of();
    }
}
