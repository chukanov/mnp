package com.eyeline.mnp.mask;

import java.util.*;

/**
 * Class represents Map<Mask,T>-like storage to store any objects by telephone number masks.
 * Based on prefix tree (https://en.wikipedia.org/wiki/Trie)
 * @param <T> generic class of stored objects
 * @author Chukanov
 */
public class MaskTrieMap<T> {

    private Node<T> root = new DigitNode<T>();

    /**
     * Stores a new object by a mask with replace old value if present
     * @param mask key
     * @param data value
     * @return old value – if presents, null – if otherwise
     */
    public T set(Mask mask, T data) {
        if (mask == null || data == null) return null;
        Node<T> node = getOrCreate(mask);
        T oldData = node.data;
        node.data = data;
        return oldData;
    }

    /**
     * Stores a new object by mask, but only if old value is not exists
     * @param mask key
     * @param data value
     * @return true – if object stored, false - if not
     */
    public boolean add(Mask mask, T data) {
        if (mask==null || data == null) return false;
        Node<T> node = getOrCreate(mask);
        T oldData = node.data;
        if (oldData!=null) return false;
        node.data = data;
        return true;
    }

    /**
     * Removes an object from a storage
     * @param mask key
     * @return old value if present, null - if not
     */
    public T remove(Mask mask) {
        if (mask == null) return null;
        Node<T> node = get(mask);
        if (node == null) return null;
        T oldData = node.data;
        node.data = null;
        return oldData;
    }

    /**
     * Lookups value by more concrete mask from the storage
     * @param mask key
     * @return value
     */
    public T lookup(Mask mask) {
        if (mask == null) return null;
        Node<T> node = root;
        Stack<Node<T>> toCheck = new Stack<>();
        for (int i=0; i<mask.length(); i++) {
            node = node.get(mask.charAt(i));
            if( node == null ) {
                break;
            }
            toCheck.push(node);
        }
        while (toCheck.size()!=0) {
            Node<T> n = toCheck.pop();
            if (n.data!=null && toCheck.size() == mask.length()-1) return n.data;
            if (n.wildcard!=null) {
                Node<T> wildNode = n.wildcard;
                int wildLenght = 0;
                while (wildNode.wildcard!=null) {
                    wildNode = wildNode.wildcard;
                    wildLenght++;
                }
                if (wildLenght+toCheck.size() == mask.length()-2)
                    return wildNode.data;
            }
        }
        return null;
    }


    private Node<T> get(Mask mask) {
        Node<T> node = root;
        for (int i=0; i<mask.length(); i++) {
            node = node.get(mask.charAt(i));
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private Node<T> getOrCreate(Mask mask) {
        Node<T> node = root;
        for (int i=0; i<mask.length(); i++) {
            node = node.getOrCreate(mask.charAt(i));
        }
        return node;
    }

    /**
     * Abstract tree node to represent symbols in the mask
     * @param <T>
     */
    static abstract class Node<T> {
        T data;
        Node<T> wildcard;

        /**
         * Get existing child by symbol
         * if not exists – create a new one digit child
         * or create WildcardNode if symbol is '?'
         */
        abstract Node<T> getOrCreate(char symbol);

        /**
         * Get child by symbol
         */
        abstract Node<T> get(char c);
    }

    /**
     * Node node represents digit symbol in the mask
     * @param <T>
     */
    private static class DigitNode<T> extends Node<T> {
        Node<T>[] childs;
        @SuppressWarnings("unchecked")
        @Override
        public Node<T> getOrCreate(char c) {
            if (c == Mask.WILDCARD) {
                if (wildcard == null) {
                    wildcard = new WildcardNode<T>();
                }
                return wildcard;
            }
            if (childs == null) {
                childs = new Node[10];
            }
            int index = Mask.digit(c);
            if (childs[index] == null) {
                childs[index] = new DigitNode<T>();
            }
            return childs[index];
        }

        @Override
        public Node<T> get(char c) {
            if (childs != null) {
                Node<T> child = childs[Mask.digit(c)];
                if (child != null) return child;
            }
            return wildcard;
        }
    }

    /**
     * Node node to represent wildcard symbol in the mask
     * @param <T>
     */

    private static class WildcardNode<T> extends Node<T> {
        @Override
        public Node<T> getOrCreate(char c) {
            if (c != Mask.WILDCARD)
                throw new IllegalArgumentException("Wildcard tree can contains wildcard sub-tree only ");
            if (wildcard == null) {
                wildcard = new WildcardNode<T>();
            }
            return wildcard;
        }

        @Override
        public Node<T> get(char c) {
            return wildcard;
        }
    }
}

