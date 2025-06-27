// src/contexts/CartContext.js
import React, { createContext, useContext, useState, useEffect } from 'react';

const CartContext = createContext();

export function CartProvider({ children }) {
  const [cart, setCart] = useState(() => {
    const stored = localStorage.getItem('cart');
    return stored ? JSON.parse(stored) : [];
  });

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cart));
  }, [cart]);

  // item = { id, name, price }, quantity = nombre de billets à ajouter
  const addToCart = (item, quantity = 1) => {
    setCart(prevCart => {
      const exist = prevCart.find(i => i.id === item.id);
      if (exist) {
        return prevCart.map(i =>
          i.id === item.id
            ? { ...i, quantity: i.quantity + quantity }
            : i
        );
      }
      return [...prevCart, { ...item, quantity }];
    });
  };

  const removeFromCart = id =>
    setCart(prevCart => prevCart.filter(i => i.id !== id));

  const updateQuantity = (id, quantity) =>
    setCart(prevCart =>
      prevCart.map(i =>
        i.id === id ? { ...i, quantity } : i
      )
    );

  const clearCart = () => setCart([]);

  return (
    <CartContext.Provider
      value={{ cart, addToCart, removeFromCart, updateQuantity, clearCart }}
    >
      {children}
    </CartContext.Provider>
  );
}

export const useCart = () => useContext(CartContext);
