package com.jizhi.geektime.rest.client;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 2021/3/28
 * jizhi7
 **/
public class DefaultVariantListBuilder extends Variant.VariantListBuilder {

    private List<Locale> locales;
    private List<String> encodings;
    private List<MediaType> mediaTypes;

    public DefaultVariantListBuilder() {
        locales = new ArrayList<>();
        encodings = new ArrayList<>();
        mediaTypes = new ArrayList<>();
    }

    @Override
    public List<Variant> build() {
        if (locales.size() == encodings.size() &&
                encodings.size() == mediaTypes.size()) {
            List<Variant> variants = new ArrayList<>();
            for (int i = 0; i < locales.size(); i++) {
                Variant variant = new Variant(mediaTypes.get(i), locales.get(i), encodings.get(i));
                variants.add(variant);
            }
            return variants;
        }
        throw new RuntimeException("数据量不一致");
    }

    @Override
    public Variant.VariantListBuilder add() {
        return this;
    }

    @Override
    public Variant.VariantListBuilder languages(Locale... languages) {
        for (Locale language : languages) {
            this.locales.add(language);
        }
        return this;
    }

    @Override
    public Variant.VariantListBuilder encodings(String... encodings) {
        for (String encoding : encodings) {
            this.encodings.add(encoding);
        }
        return this;
    }

    @Override
    public Variant.VariantListBuilder mediaTypes(MediaType... mediaTypes) {
        for (MediaType mediaType : mediaTypes) {
            this.mediaTypes.add(mediaType);
        }
        return this;
    }
}
