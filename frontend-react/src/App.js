import './App.css';

import Container from 'react-bootstrap/Container';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Button from 'react-bootstrap/Button';
import NavbarCustom from './components/Navbar'
import Suppliers from './services/SupplierList'
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Switch from 'react-bootstrap/esm/Switch';
import Home from './services/Home';



function App() {
  return (
    <>
        <NavbarCustom fixed="top" />
        <Switch>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/suppliers" element={<Suppliers />} />
          </Routes>
        </Switch>
    </>
  );
}

export default App;
