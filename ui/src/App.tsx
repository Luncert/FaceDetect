import React from 'react';
import './App.css';

const App: React.FC = () => {
  return (
    <div className='App'>
      <div className='App-Background'></div>
      <div className='App-Container'>
        <div className='Title'>Face Detect</div>
        <canvas className='VideoCanvas'></canvas>
        <div className='IdentifiedList'></div>
      </div>
    </div>
  );
}

export default App;
