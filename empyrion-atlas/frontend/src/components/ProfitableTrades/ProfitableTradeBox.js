import React, { useState, useEffect } from 'react';
import '../../styles/components/ProfitableTradeBox.css';

const ProfitableTradeBox = ({ tradeInfo, collapseTrigger, onCollapseAll, collapseMode, setCollapseMode }) => {
  const [expanded, setExpanded] = useState(false);

  useEffect(() => {
    setExpanded(false);
  }, [collapseTrigger]);

  const handleClick = () => {
    if (expanded) {
      setExpanded(false);
      setCollapseMode(false);
    } else {
      if (collapseMode) {
        onCollapseAll();
        setTimeout(() => setExpanded(true), 0);
      } else {
        setExpanded(true);
        setCollapseMode(true);
      }
    }
  };

  return (
    <div className="trade-box" onClick={handleClick}>
      <div className="trade-box-header centered-header">
        <h3 className="highlighted-item">{tradeInfo.itemName}</h3>
        <p className="trade-box-sub">
          Total Profit: <span className="profit-value">{Math.round(tradeInfo.totalProfit)} cr</span> — {tradeInfo.amount} units
        </p>
      </div>

      <div className="trade-columns">
        <div className="trade-column">
          <div className="trade-label">Buy From</div>
          <div className="trade-trader">{tradeInfo.sellTrader}</div>

          {expanded && (
            <ul className="station-list">
              {tradeInfo.sellStations.map((station, i) => (
                <li key={i}>{station}</li>
              ))}
            </ul>
          )}
        </div>

        <div className="trade-column">
          <div className="trade-label">Sell To</div>
          <div className="trade-trader">{tradeInfo.buyTrader}</div>

          {expanded && (
            <ul className="station-list">
              {tradeInfo.buyStations.map((station, i) => (
                <li key={i}>{station}</li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProfitableTradeBox;