package com.angazo.arume.core.module;

import java.util.Collection;

public interface FiscalModule {

    FiscalModuleDescriptor descriptor();

    Collection<? extends FiscalCapability> capabilities();
}
