import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import '../../styles/EmpyrionTheme.css';

const Navbar = () => {
  const location = useLocation();

  return (
    <header className="empyrion-navbar">
      <div className="navbar-title">Empyrion Atlas</div>
      <nav className="navbar-links">
        <Link to="/" className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}>Trade Planner</Link>
        <Link to="/items" className={`nav-link ${location.pathname === '/items' ? 'active' : ''}`}>Item Lookup</Link>
        <Link to="/about" className={`nav-link ${location.pathname === '/about' ? 'active' : ''}`}>About</Link>
      </nav>
    </header>
  );
};

export default Navbar;