import React from 'react';
import { Routes, Route } from 'react-router-dom';
import TradePlannerPage from './pages/TradePlannerPage';
import AboutPage from './pages/AboutPage';
import ItemSearchPage from './pages/ItemSearchPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<TradePlannerPage />} />
	  <Route path="/items" element={<ItemSearchPage />} />
      <Route path="/about" element={<AboutPage />} />
    </Routes>
  );
}

export default App;