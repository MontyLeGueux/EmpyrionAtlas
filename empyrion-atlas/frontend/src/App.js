import React from 'react';
import { Routes, Route } from 'react-router-dom';
import AboutPage from './pages/AboutPage';
import ProfitableTradesPage from './pages/ProfitableTradesSearchPage';
import ItemSearchPage from './pages/ItemSearchPage';
import HomePage from './pages/HomePage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
	  <Route path="/items" element={<ItemSearchPage />} />
	  <Route path="/trades" element={<ProfitableTradesPage />} />
      <Route path="/about" element={<AboutPage />} />
    </Routes>
  );
}

export default App;