import React, { useEffect, useState } from 'react';
import PageWrapper from '../components/Layout/PageWrapper';
import ProfitableTradeBox from '../components/ProfitableTrades/ProfitableTradeBox';
import axios from 'axios';
import '../styles/EmpyrionTheme.css';

function ProfitableTradesPage() {
  const [trades, setTrades] = useState([]);
  const [collapseTrigger, setCollapseTrigger] = useState(false);
  const [collapseMode, setCollapseMode] = useState(false);

useEffect(() => {
  const fetchTrades = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/profitable-trades');
      setTrades(response.data);
    } catch (error) {
      console.error('Failed to fetch profitable trades:', error);
    }
  };

  fetchTrades();
}, []);

const handleCollapseAll = () => {
    setCollapseTrigger(!collapseTrigger);
  };

  return (
	<PageWrapper>
	<h1 className="profit-title">Profitable Trade Routes</h1>
	<p>Most profitable trades in the game sorted by highest to lowest total profits.</p>
	<p>Profits and unit values are averaged.</p>

	      {trades.map((trade, index) => (
	        <ProfitableTradeBox
	          key={index}
	          tradeInfo={trade}
	          collapseTrigger={collapseTrigger}
	          onCollapseAll={handleCollapseAll}
	          collapseMode={collapseMode}
	          setCollapseMode={setCollapseMode}
	        />
	      ))}
	</PageWrapper>
  );
}

export default ProfitableTradesPage;
