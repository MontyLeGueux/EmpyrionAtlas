import React, { useState, useEffect } from 'react';
import '../../styles/components/ItemTradeBox.css';

const ItemTradeBox = ({ tradeInfo, collapseTrigger, onCollapseAll, collapseMode, setCollapseMode }) => {
  const [expanded, setExpanded] = useState(false);

  const price = tradeInfo.price;
  const volume = tradeInfo.volume;

  useEffect(() => {
      setExpanded(false);
    }, [collapseTrigger]);

	const handleClick = () => {
	    if (expanded) {
	      setExpanded(false);
		  setCollapseMode(false);
	    } else {
			if(collapseMode){
				onCollapseAll();
				setTimeout(() => setExpanded(true), 0);
			}
			else{
				setExpanded(true);
				setCollapseMode(true);
			}
	    }
	};
	
  return (
    <div className="trade-box" onClick={handleClick}>
      <div className="trade-box-header">
        <div>
          <h3>{tradeInfo.traderName}</h3>
          <p className="trade-box-sub">
            <span className="profit-value">{Math.round(price)} cr</span> - {volume} units
          </p>
        </div>
        <span className="toggle">{expanded ? '▾' : '▸'}</span>
      </div>

      {expanded && (
        <ul className="station-list">
          {tradeInfo.traders.map((t, i) => (
            <li key={i}>
              {t.stationName} <span className="restock">(restock: {t.restockTimer})</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ItemTradeBox;