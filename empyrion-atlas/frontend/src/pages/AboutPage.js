import React from 'react';
import PageWrapper from '../components/Layout/PageWrapper';
import '../styles/EmpyrionTheme.css';

const AboutPage = () => {
  return (
    <PageWrapper>
      <div className="about-container">
        <h1 className="about-title">About Empyrion Atlas</h1>

        <div className="about-section">
          <h2 className="section-title">Why Empyrion Atlas Exists</h2>
          <p className="about-paragraph">
            <strong>Empyrion Galactic Survival</strong> is a vast and complex sandbox experience, but it often leaves players to navigate its sprawling systems without much in-game guidance — especially when it comes to trading. Prices, availability, and even trader locations are left for players to piece together on their own. <span className="highlight">Empyrion Atlas</span> was created to bring clarity to this process, offering a structured way to evaluate profitable trades, and ultimately reduce the guesswork in an otherwise obscure part of the game.
          </p>
        </div>

        <div className="about-section">
          <h2 className="section-title">A Fullstack Portfolio Project</h2>
          <p className="about-paragraph">
            <span className="highlight">Empyrion Atlas</span> is also a technical showcase — a small scale project built as a full-stack application using <strong>Java</strong>, <strong>Spring Boot</strong>, <strong>Hibernate</strong>, <strong>Maven</strong>, <strong>PostgreSQL</strong>, and <strong>React</strong>. It was developed as a portfolio piece as a mostly self taught fullstack dev. The source code can be found on github below.
          </p>
        </div>
      </div>
    </PageWrapper>
  );
};

export default AboutPage;