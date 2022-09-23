package com.link_intersystems.lang;

import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author René Link {@literal <rene.link@link-intersystems.com>}
 */
public class CodeSourceLocationResolverChain extends AbstractList<CodeSourceLocationResolver> implements CodeSourceLocationResolver {

    private List<CodeSourceLocationResolver> resolverList = new ArrayList<>();

    @Override
    public void add(int index, CodeSourceLocationResolver element) {
        resolverList.add(index, element);
    }

    @Override
    public CodeSourceLocationResolver set(int index, CodeSourceLocationResolver element) {
        return resolverList.set(index, element);
    }

    @Override
    public CodeSourceLocationResolver remove(int index) {
        return resolverList.remove(index);
    }

    @Override
    public Optional<URL> resolve(Class<?> clazz) {
        return stream().map(r -> r.resolve(clazz)).filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    @Override
    public CodeSourceLocationResolver get(int index) {
        return resolverList.get(index);
    }

    @Override
    public int size() {
        return resolverList.size();
    }
}
