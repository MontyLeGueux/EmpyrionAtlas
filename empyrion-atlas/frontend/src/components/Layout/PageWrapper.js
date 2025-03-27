import React from 'react';
import Navbar from './Navbar';
import '../../styles/EmpyrionTheme.css';

const PageWrapper = ({ children }) => {
  return (
    <div className="empyrion-page">
      <Navbar />
      <div className="empyrion-scroll-wrapper">
        <main className="empyrion-main">
          {children}
        </main>
      </div>
      <footer className="empyrion-footer">
        A third party tool for <strong>Empyrion Galactic Survival</strong> with the scenario <strong>Reforged Eden 2</strong>
      </footer>
    </div>
  );
};

export default PageWrapper;