unit CompoundOperators;

interface

implementation

uses
  System.Classes;

procedure TestIsNot;
var
  Obj: TObject;
begin
  if Obj is not TComponent then
    Exit;
end;

procedure TestNotIn;
type
  TFruit = (Apple, Orange, Banana);
var
  F: TFruit;
begin
  if F not in [Apple, Orange] then
    Exit;
end;

procedure TestUnaryNotRegression;
var
  Obj: TObject;
begin
  if not (Obj is TComponent) then
    Exit;
end;

end.
