import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [itemName, setItemName] = useState('');
  const [sellers, setSellers] = useState([]);
  const [buyers, setBuyers] = useState([]);
  const [error, setError] = useState('');

  const handleSearch = async () => {
    if (!itemName.trim()) return;

    setError('');
    setSellers([]);
    setBuyers([]);

    try {
      const response = await axios.get(`http://localhost:8080/api/items/${itemName}`);

      const data = response.data;

      const sellList = [];
      const buyList = [];

      data.forEach((entry) => {
        const traderName = entry.trader?.traderName || "Unknown Trader";

        if (entry.avgSellPrice > 0 && entry.avgSellVolume > 0) {
          sellList.push({
            traderName,
            price: entry.avgSellPrice,
            volume: entry.avgSellVolume
          });
        }

        if (entry.avgBuyPrice > 0 && entry.avgBuyVolume > 0) {
          buyList.push({
            traderName,
            price: entry.avgBuyPrice,
            volume: entry.avgBuyVolume
          });
        }
      });

      setSellers(sellList);
      setBuyers(buyList);
    } catch (err) {
      console.error(err);
      setError('An Error Occured');
    }
  };

  return (
    <div className="container">
      <h1>Trade Lookup</h1>

      <div className="search-bar">
        <input
          type="text"
          placeholder="Enter item name"
          value={itemName}
          onChange={(e) => setItemName(e.target.value)}
        />
        <button onClick={handleSearch}>Search</button>
      </div>

      {error && <p className="error">❌ {error}</p>}

      {(sellers.length > 0 || buyers.length > 0) && (
        <div className="results">
          <div className="column">
            <h2>Sell Offers</h2>
            {sellers.length === 0 ? (
              <p>No sell offers</p>
            ) : (
              <ul>
                {sellers.map((seller, index) => (
                  <li key={index}>
                    <strong>{seller.traderName}</strong><br />
                    Avg Price: {seller.price.toFixed(2)}<br />
                    Avg Volume: {seller.volume}
                  </li>
                ))}
              </ul>
            )}
          </div>

          <div className="column">
            <h2>Buy Offers</h2>
            {buyers.length === 0 ? (
              <p>No buy offers</p>
            ) : (
              <ul>
                {buyers.map((buyer, index) => (
                  <li key={index}>
                    <strong>{buyer.traderName}</strong><br />
                    Avg Price: {buyer.price.toFixed(2)}<br />
                    Avg Volume: {buyer.volume}
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
