import { ChangeEvent, useEffect, useMemo } from "react";
import { Link } from "react-router-dom";
import { Category } from "../../commons/types";
import CategoryService from "../../services/CategoryService";

interface TableProps {
  list: any[];
  filter: string;
  theme: string;
}

export function Table({ list, filter, theme }: TableProps) {
  
  let filteredList = useMemo(
    () => { return CategoryService.filterCategories(list, filter)},
    [list, filter]
  );
   
  return (
    <>
      <table className={theme}>
        <thead>
          <tr>
            <th>Código</th>
            <th>Nome</th>
          </tr>
        </thead>
        <tbody>
          {filteredList.map((category: Category) => (
            <tr key={category.id}>
              <td>{category.id}</td>
              <td>{category.name}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
