package com.netsuite.chinalocalization.page.voucher.template

import com.google.inject.AbstractModule
import com.google.inject.name.Names

/**
 * mia.wang@oracle.com
 * Mar/9/2018
 */
class TemplateModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(VoucherTemplate.class).annotatedWith(Names.named("MultiCurrencies")).to(MultiCurrenciesTemplate.class);
        bind(VoucherTemplate.class).annotatedWith(Names.named("SingleCurrency")).to(SingleCurrencyTemplate.class);
    }
}
