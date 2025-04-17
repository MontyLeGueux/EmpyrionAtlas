import React, { useState } from 'react';
import axios from 'axios';
import PageWrapper from '../components/Layout/PageWrapper';
import ItemTradeBox from '../components/ItemLookup/ItemTradeBox';
import '../styles/EmpyrionTheme.css';

const ItemSearchPage = () => {
  const [itemName, setItemName] = useState('');
  const [suggestions, setSuggestions] = useState([]);
  const [buyers, setBuyers] = useState([]);
  const [sellers, setSellers] = useState([]);
  const [error, setError] = useState('');
  const [collapseTrigger, setCollapseTrigger] = useState(0);
  const [collapseMode, setCollapseMode] = useState(false);

  const fetchSuggestions = async (query) => {
    if (query.length < 2) {
      setSuggestions([]);
      return;
    }

    try {
      const res = await axios.get(`/api/items/suggest?query=${query}`);
      setSuggestions(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleSearch = async (nameOverride) => {
    const searchQuery = nameOverride ?? itemName;

    if (!searchQuery.trim()) return;

    setError('');
    setSellers([]);
    setBuyers([]);

    try {
      const response = await axios.get(`/api/items/${searchQuery}`);
      const { itemExists, trades } = response.data;
	  
	  if(!itemExists){
		setError("This item was not found in the database.");
		return;
	  }
	  
	  if(trades.length === 0){
  		setError("There are no trades associated with this item.");
  		return;
	  }

      const sellersList = [];
      const buyersList = [];

      trades.forEach((entry) => {
        const traderName = entry.traders?.[0]?.traderName || 'Unknown';
        if (entry.avgSellPrice > 0 && entry.avgSellVolume > 0) {
          sellersList.push({ traderName, price: entry.avgSellPrice, volume: entry.avgSellVolume, traders: entry.traders });
        }

        if (entry.avgBuyPrice > 0 && entry.avgBuyVolume > 0) {
          buyersList.push({ traderName, price: entry.avgBuyPrice, volume: entry.avgBuyVolume, traders: entry.traders });
        }
      });

      setSellers(sellersList);
      setBuyers(buyersList);
    } catch (err) {
      console.error(err);
      setError("Error fetching trade data");
    }
  };

  const handleSuggestionClick = (name) => {
    setItemName(name);
    setSuggestions([]);
    handleSearch(name);
  };

  return (
    <PageWrapper>
      <div className="empyrion-header">
        <h1>Item Lookup</h1>
        <p className="subtitle">Search for an item to see who buys and sells it</p>
      </div>

      <div className="search-section">
		  <input
		    type="text"
		    placeholder="Enter item name"
		    value={itemName}
		    onChange={(e) => {
		      const val = e.target.value;
		      setItemName(val);
		      fetchSuggestions(val);
		    }}
		    onBlur={() => {
		      setTimeout(() => setSuggestions([]), 150); // slight delay for click to register
		    }}
		    onFocus={() => fetchSuggestions(itemName)}
		  />
        <button onClick={() => handleSearch()}>Search</button>

        {suggestions.length > 0 && (
          <ul className="suggestions">
            {suggestions.map((s, i) => (
              <li key={i} onMouseDown={() => handleSuggestionClick(s)}>
                {s}
              </li>
            ))}
          </ul>
        )}
      </div>

      {error && <p className="error-text">{error}</p>}

	  {(buyers.length > 0 || sellers.length > 0) && (
	    <div className="results-grid full-width">
	      <div>
	        <h2>Sellers</h2>
	        {sellers.map((entry, i) => (
	          <ItemTradeBox 
			  	key={`seller-${i}`} 
			  	tradeInfo={entry} 
			 	collapseTrigger={collapseTrigger} 
			  	onCollapseAll={() => setCollapseTrigger(prev => prev + 1)}
				collapseMode={collapseMode}
				setCollapseMode={setCollapseMode}
			  />
	        ))}
	      </div>
	      <div>
	        <h2>Buyers</h2>
	        {buyers.map((entry, i) => (
	          <ItemTradeBox 
			  	key={`buyer-${i}`} 
			  	tradeInfo={entry}			  
			  	collapseTrigger={collapseTrigger} 
				onCollapseAll={() => setCollapseTrigger(prev => prev + 1)}
				collapseMode={collapseMode}
				setCollapseMode={setCollapseMode}
			  />
	        ))}
	      </div>
	    </div>
	  )}
    </PageWrapper>
  );
};

export default ItemSearchPage;