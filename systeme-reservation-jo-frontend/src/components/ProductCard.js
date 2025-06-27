// src/components/ProductCard.js
import React from 'react';
import { useCart } from '../contexts/CartContext';

function ProductCard({ product }) {
  const { addToCart } = useCart();

  return (
    <div className="product-card">
      <h3>{product.name}</h3>
      <p>{product.price} €</p>
      <button onClick={() => addToCart(product)}>
        Ajouter au panier
      </button>
    </div>
  );
}

export default ProductCard;
