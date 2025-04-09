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
	    <p>
	      A third-party tool for <strong>Empyrion Galactic Survival</strong> using the scenario <strong>Reforged Eden 2</strong>.   Built by <a href="https://github.com/MontyLeGueux" target="_blank" rel="noopener noreferrer">MontyLeGueux</a> — view the project on <a href="https://github.com/MontyLeGueux/EmpyrionAtlas" target="_blank" rel="noopener noreferrer">GitHub</a>.
	    </p>
	  </footer>
    </div>
  );
};

export default PageWrapper;