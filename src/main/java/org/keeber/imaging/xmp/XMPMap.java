package org.keeber.imaging.xmp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;

/**
 * Represents an XMP Metadata packet: `<x:xmpmeta>`.
 */
public class XMPMap {

    public static class XMPNamespace {
        @Getter
        private String prefix, uri;

        XMPNamespace(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }

        public String toString() {
            return  String.format("xmlns:%s='%s'", prefix, uri);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            return Objects.equals(this.uri, ((XMPNamespace) obj).uri);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.uri);
        }

    }

    @Getter private Set<XMPNamespace> namespaces = new HashSet<>();

    @Getter private Map<String,Object> properties = new HashMap<>();

}
