import './App.css';

import NavbarCustom from './components/Navbar'
import Suppliers from './services/SupplierList'
import { Route, Routes } from 'react-router-dom';
import Switch from 'react-bootstrap/esm/Switch';
import Home from './services/Home';
import SupplierCreate from './services/SupplierCreate';



function App() {
  return (
    <>
        <NavbarCustom fixed="top" />
        <Switch>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/suppliers" element={<Suppliers />} />
            <Route path="/suppliers/create" element={<SupplierCreate />} />
          </Routes>
        </Switch>
    </>
  );
}

export default App;
